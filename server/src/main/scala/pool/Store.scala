package pool

import com.github.blemale.scaffeine.{Cache, Scaffeine}
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import com.zaxxer.hikari.HikariDataSource

import java.time.LocalDate
import javax.sql.DataSource

import scalikejdbc.*
import scala.concurrent.duration.FiniteDuration

object Store:
  def cache(minSize: Int,
            maxSize: Int,
            expireAfter: FiniteDuration): Cache[String, String] =
    Scaffeine()
      .initialCapacity(minSize)
      .maximumSize(maxSize)
      .expireAfterWrite(expireAfter)
      .build[String, String]()

final class Store(config: Config,
                  cache: Cache[String, String]) extends LazyLogging:
  private val dataSource: DataSource = {
    val ds = new HikariDataSource()
    ds.setDataSourceClassName(config.getString("db.driverClassName"))
    ds.addDataSourceProperty("url", config.getString("db.url"))
    ds.addDataSourceProperty("user", config.getString("db.user"))
    ds.addDataSourceProperty("password", config.getString("db.password"))
    ds
  }
  ConnectionPool.singleton(new DataSourceConnectionPool(dataSource))

  def register(account: Account): Account = addAccount(account)

  def login(email: String, pin: String): Option[Account] =
    DB readOnly { implicit session =>
      sql"select * from account where email_address = $email and pin = $pin"
      .map(rs =>
        Account(
          rs.long("id"),
          rs.string("license"),
          rs.string("email_address"),
          rs.string("pin"),
          rs.long("activated"),
          rs.long("deactivated")
        )
      )
      .single()
    }

  def isEmailAddressUnique(emailAddress: String): Boolean =
    val count = DB readOnly { implicit session =>
      sql"select id from account where email_address = $emailAddress"
      .map(rs => rs.long("id"))
      .single()
    }
    if count.isDefined then false else true

  def isAuthorized(license: String): Boolean =
    cache.getIfPresent(license) match
      case Some(_) =>
        logger.debug(s"*** store cache get: $license")
        true
      case None =>
        val optionalLicense = DB readOnly { implicit session =>
          sql"select license from account where license = $license"
          .map(rs => rs.string("license"))
          .single()
        }
        if optionalLicense.isDefined then
          cache.put(license, license)
          logger.debug(s"*** store cache put: $license")
          true
        else false

  def listAccounts(): List[Account] =
    DB readOnly { implicit session =>
      sql"select * from account"
      .map(rs =>
        Account(
          rs.long("id"),
          rs.string("license"),
          rs.string("email_address"),
          rs.string("pin"),
          rs.long("activated"),
          rs.long("deactivated")
        )
      )
      .list()
    }

  def addAccount(account: Account): Account =
    val id = DB localTx { implicit session =>
      sql"""
          insert into account(license, email_address, pin, activated, deactivated) 
          values(${account.license}, ${account.emailAddress}, ${account.pin}, ${account.activated}, ${account.deactivated})
         """
      .updateAndReturnGeneratedKey()
    }
    account.copy(id = id)

  def removeAccount(license: String): Unit =
    DB localTx { implicit session =>
      sql"delete account where license = $license"
      .update()
    }
    ()

  def deactivateAccount(license: String): Option[Account] =
    DB localTx { implicit session =>
      val deactivated = sql"update account set deactivated = ${LocalDate.now.toEpochDay}, activated = 0 where license = $license"
                        .update()
      if deactivated > 0 then
        sql"select * from account where license = $license"
        .map(rs =>
          Account(
            rs.long("id"),
            rs.string("license"),
            rs.string("email_address"),
            rs.string("pin"),
            rs.long("activated"),
            rs.long("deactivated")
          )
        )
        .single()
      else None
    }

  def reactivateAccount(license: String): Option[Account] =
    DB localTx { implicit session =>
      val activated = sql"update account set activated = ${LocalDate.now.toEpochDay}, deactivated = 0 where license = $license"
                      .update()
      if activated > 0 then
        sql"select * from account where license = $license"
        .map(rs =>
          Account(
            rs.long("id"),
            rs.string("license"),
            rs.string("email_address"),
            rs.string("pin"),
            rs.long("activated"),
            rs.long("deactivated")
          )
        )
        .single()
      else None
    }

  def listPools(license: String): List[Pool] = DB readOnly { implicit session =>
    sql"select * from pool where license = $license order by name"
    .map(rs =>
      Pool(
        rs.long("id"),
        rs.string("license"),
        rs.string("name"), 
        rs.int("volume"), 
        rs.string("unit")
      )
    )
    .list()
  }

  def addPool(pool: Pool): Long = DB localTx { implicit session =>
    sql"""
        insert into pool(name, license, volume, unit) values(${pool.name}, ${pool.license}, ${pool.volume}, ${pool.unit.toString})
       """
    .updateAndReturnGeneratedKey()
  }

  def updatePool(pool: Pool): Long = DB localTx { implicit session =>
    sql"""
        update pool set name = ${pool.name}, volume = ${pool.volume}, unit = ${pool.unit.toString}
        where id = ${pool.id}
       """
    .update()
    pool.id
  }

  def listCleanings(poolId: Long): List[Cleaning] = DB readOnly { implicit session =>
    sql"select * from cleaning where pool_id = $poolId order by cleaned desc"
    .map(rs =>
      Cleaning(
        rs.long("id"),
        rs.long("pool_id"),
        rs.boolean("brush"),
        rs.boolean("net"),
        rs.boolean("skimmer_basket"),
        rs.boolean("pump_basket"),
        rs.boolean("pump_filter"),
        rs.boolean("vacuum"),
        rs.long("cleaned")
      )
    )
    .list()
  }

  def addCleaning(cleaning: Cleaning): Long = DB localTx { implicit session =>
    sql"""
        insert into cleaning(pool_id, brush, net, skimmer_basket, pump_basket, pump_filter, vacuum, cleaned)
        values(${cleaning.poolId}, ${cleaning.brush}, ${cleaning.net}, ${cleaning.skimmerBasket},
        ${cleaning.pumpBasket}, ${cleaning.pumpFilter}, ${cleaning.vacuum}, ${cleaning.cleaned})
       """
    .updateAndReturnGeneratedKey()
  }

  def updateCleaning(cleaning: Cleaning): Long = DB localTx { implicit session =>
    sql"""
        update cleaning set brush = ${cleaning.brush}, net = ${cleaning.net}, skimmer_basket = ${cleaning.skimmerBasket},
        pump_basket = ${cleaning.pumpBasket}, pump_filter = ${cleaning.pumpFilter}, vacuum = ${cleaning.vacuum},
        cleaned = ${cleaning.cleaned} where id = ${cleaning.id}
      """
    .update()
    cleaning.id
  }

  def listMeasurements(poolId: Long): List[Measurement] = DB readOnly { implicit session =>
    sql"select * from measurement where pool_id = $poolId order by measured desc"
    .map(rs =>
      Measurement(
        rs.long("id"),
        rs.long("pool_id"),
        rs.int("total_chlorine"),
        rs.int("free_chlorine"),
        rs.double("combined_chlorine"),
        rs.double("ph"),
        rs.int("calcium_hardness"),
        rs.int("total_alkalinity"),
        rs.int("cyanuric_acid"),
        rs.int("total_bromine"),
        rs.int("salt"),
        rs.int("temperature"),
        rs.long("measured")
      )
    )
    .list()
  }

  def addMeasurement(measurement: Measurement): Long = DB localTx { implicit session =>
    sql"""
        insert into measurement(pool_id, total_chlorine, free_chlorine, combined_chlorine, ph, calcium_hardness,
        total_alkalinity, cyanuric_acid, total_bromine, salt, temperature, measured)
        values(${measurement.poolId}, ${measurement.totalChlorine}, ${measurement.freeChlorine}, ${measurement.combinedChlorine},
        ${measurement.ph}, ${measurement.calciumHardness}, ${measurement.totalAlkalinity}, ${measurement.cyanuricAcid},
        ${measurement.totalBromine}, ${measurement.salt}, ${measurement.temperature}, ${measurement.measured})
      """
    .updateAndReturnGeneratedKey()
  }

  def updateMeasurement(measurement: Measurement): Long = DB localTx { implicit session =>
    sql"""
        update measurement set total_chlorine = ${measurement.totalChlorine}, free_chlorine = ${measurement.freeChlorine},
        combined_chlorine = ${measurement.combinedChlorine}, ph = ${measurement.ph}, calcium_hardness = ${measurement.calciumHardness},
        total_alkalinity = ${measurement.totalAlkalinity}, cyanuric_acid = ${measurement.cyanuricAcid},
        total_bromine = ${measurement.totalBromine}, salt = ${measurement.salt}, temperature = ${measurement.temperature},
        measured = ${measurement.measured}
        where id = ${measurement.id}
      """
    .update()
    measurement.id
  }

  def listChemicals(poolId: Long): List[Chemical] = DB readOnly { implicit session =>
    sql"select * from chemical where pool_id = $poolId order by added desc"
    .map(rs =>
      Chemical(
        rs.long("id"),
        rs.long("pool_id"),
        rs.string("chemical"),
        rs.double("amount"),
        rs.string("unit"),
        rs.long("added")
      )
    )
    .list()
  }

  def addChemical(chemical: Chemical): Long = DB localTx { implicit session =>
    sql"""
        insert into chemical(pool_id, chemical, amount, unit, added)
        values(${chemical.poolId}, ${chemical.chemical}, ${chemical.amount}, ${chemical.unit}, ${chemical.added})
      """
      .updateAndReturnGeneratedKey()
  }

  def updateChemical(chemical: Chemical): Long = DB localTx { implicit session =>
    sql"""
        update chemical set chemical = ${chemical.chemical}, amount = ${chemical.amount}, unit = ${chemical.unit},
        added = ${chemical.added} where id = ${chemical.id}
      """
      .update()
    chemical.id
  }

  def listFaults(): List[Fault] = DB readOnly { implicit session =>
    sql"select * from fault order by occurred desc"
    .map(rs =>
      Fault(
        rs.string("cause"),
        rs.string("occurred")
      )
    )
    .list()
  }

  def addFault(fault: Fault): Fault = DB localTx { implicit session =>
    sql"insert into fault(cause, occurred) values(${fault.cause}, ${fault.occurred})"
    fault
  }