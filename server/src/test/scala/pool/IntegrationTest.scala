package pool

import com.typesafe.config.ConfigFactory

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration.*
import scala.sys.process.Process

import Validator.*

class IntegrationTest extends AnyFunSuite with Matchers:
  val exitCode = Process("psql -d poolbalance -f ddl.sql").run().exitValue()
  exitCode shouldBe 0

  val config = ConfigFactory.load("test.conf")

  val store = Store(config, Store.cache(minSize = 1, maxSize = 1, expireAfter = 1.hour))
  val emailer = Emailer(config)
  val dispatcher = Dispatcher(store, emailer)

  var testAccount = Account()
  var testPool = Pool()
  var testCleaning = Cleaning(poolId = 0)
  var testMeasurement = Measurement(poolId = 0)
  var testChemical = Chemical(poolId = 0)

  test("integration") {
    register
    login

    deactivate
    reactivate

    addPool
    updatePool
    listPools

    addCleaning
    updateCleaning
    listCleanings

    addMeasurement
    updateMeasurement
    listMeasurements

    addChemical
    updateChemical
    listChemicals
  }

  def register: Unit =
    val register = Register(config.getString("email.sender"))
    dispatcher.dispatch(register) match
      case Registered(account) =>
        assert( account.isActivated )
        testAccount = account
      case fault => fail(s"Invalid registered event: $fault")
    
  def login: Unit =
    val login = Login(testAccount.emailAddress, testAccount.pin)
    dispatcher.dispatch(login) match
      case LoggedIn(account) => account shouldBe testAccount
      case fault => fail(s"Invalid loggedin event: $fault")

  def deactivate: Unit =
    val deactivate = Deactivate(testAccount.license)
    dispatcher.dispatch(deactivate) match
      case Deactivated(account) => assert( account.isDeactivated )
      case fault => fail(s"Invalid deactivated event: $fault")

  def reactivate: Unit =
    val reactivate = Reactivate(testAccount.license)
    dispatcher.dispatch(reactivate) match
      case Reactivated(account) => assert( account.isActivated )
      case fault => fail(s"Invalid reactivated event: $fault")

  def addPool: Unit =
    testPool = Pool(id = 0, license = testAccount.license, name = "a", volume = 8000, unit = UnitOfMeasure.gl.toString)
    val addPool = AddPool(testAccount.license, testPool)
    dispatcher.dispatch(addPool) match
      case PoolAdded(pool) =>
        pool.id should not be 0
        testPool = testPool.copy(id = pool.id)
        testCleaning = testCleaning.copy(poolId = pool.id)
        testMeasurement = testMeasurement.copy(poolId = pool.id)
        testChemical = testChemical.copy(poolId = pool.id)
      case fault => fail(s"Invalid pool saved event: $fault")

  def updatePool: Unit =
    testPool = testPool.copy(volume = 10000)
    val updatePool = UpdatePool(testAccount.license, testPool)
    dispatcher.dispatch(updatePool) match
      case Updated() =>
      case fault => fail(s"Invalid pool saved event: $fault")
    
  def listPools: Unit =
    val listPools = ListPools(testAccount.license)
    dispatcher.dispatch(listPools) match
      case PoolsListed(pools) =>
        pools.length shouldBe 1
        pools.head shouldBe testPool
      case fault => fail(s"Invalid pools listed event: $fault")

  def addCleaning: Unit =
    val addCleaning = AddCleaning(testAccount.license, testCleaning)
    dispatcher.dispatch(addCleaning) match
      case CleaningAdded(cleaning) =>
        cleaning.id should not be 0
        testCleaning = testCleaning.copy(id = cleaning.id)
      case fault => fail(s"Invalid cleaning saved event: $fault")

  def updateCleaning: Unit =
    testCleaning = testCleaning.copy(vacuum = true)
    val updateCleaning = UpdateCleaning(testAccount.license, testCleaning)
    dispatcher.dispatch(updateCleaning) match
      case Updated() =>
      case fault => fail(s"Invalid cleaning saved event: $fault")

  def listCleanings: Unit =
    val listCleanings = ListCleanings(testAccount.license, testPool.id)
    dispatcher.dispatch(listCleanings) match
      case CleaningsListed(cleanings) =>
        cleanings.length shouldBe 1
        cleanings.head shouldBe testCleaning
      case fault => fail(s"Invalid cleanings listed event: $fault")
    
  def addMeasurement: Unit =
    val addMeasurement = AddMeasurement(testAccount.license, testMeasurement)
    dispatcher.dispatch(addMeasurement) match
      case MeasurementAdded(measurement) =>
        measurement.id should not be 0
        testMeasurement = testMeasurement.copy(id = measurement.id)
      case fault => fail(s"Invalid measurement saved event: $fault")

  def updateMeasurement: Unit =
    testMeasurement = testMeasurement.copy(temperature = 82)
    val updateMeasurement = UpdateMeasurement(testAccount.license, testMeasurement)
    dispatcher.dispatch(updateMeasurement) match
      case Updated() =>
      case fault => fail(s"Invalid measurement saved event: $fault")

  def listMeasurements: Unit =
    val listMeasurements = ListMeasurements(testAccount.license, testPool.id)
    dispatcher.dispatch(listMeasurements) match
      case MeasurementsListed(measurements) =>
        measurements.length shouldBe 1
        measurements.head shouldBe testMeasurement
      case fault => fail(s"Invalid measurements listed event: $fault")

  def addChemical: Unit =
    val saveChemical = SaveChemical(testAccount.license, testChemical)
    dispatcher.dispatch(saveChemical) match
      case ChemicalSaved(id) =>
        id should not be 0
        testChemical = testChemical.copy(id = id)
      case fault => fail(s"Invalid chemical saved event: $fault")    

  def updateChemical: Unit =
    testChemical = testChemical.copy(amount = 2.0)
    val saveChemical = SaveChemical(testAccount.license, testChemical)
    dispatcher.dispatch(saveChemical) match
      case ChemicalSaved(id) => id shouldBe testChemical.id
      case fault => fail(s"Invalid chemical saved event: $fault")

  def listChemicals: Unit =
    val listChemicals = ListChemicals(testAccount.license, testPool.id)
    dispatcher.dispatch(listChemicals) match
      case ChemicalsListed(chemicals) =>
        chemicals.length shouldBe 1
        chemicals.head shouldBe testChemical
      case fault => fail(s"Invalid chemicals listed event: $fault")