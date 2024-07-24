package pool

import java.util.UUID

import scala.util.Try
import scala.util.control.NonFatal

import Validator.*

sealed trait Security
case object Authorized extends Security
final case class Unauthorized(cause: String) extends Security

final class Dispatcher(store: Store, emailer: Emailer):
  def dispatch[E <: Event](command: Command): Event =
    command.isValid match
      case false => Fault(s"Command is invalid: $command")
      case true =>
        isAuthorized(command) match
          case Authorized(isAuthorized, message) => if !isAuthorized then Fault(message)

        command match
          case Register(emailAddress)            => register(emailAddress)
          case Login(emailAddress, pin)          => login(emailAddress, pin)
          case Deactivate(license)               => deactivateAccount(license)
          case Reactivate(license)               => reactivateAccount(license)
          case ListPools(license)                => listPools(license)
          case AddPool(_, pool)                  => addPool(pool)
          case UpdatePool(_, pool)               => updatePool(pool)
          case ListCleanings(_, poolId)          => listCleanings(poolId)
          case AddCleaning(_, cleaning)          => addCleaning(cleaning)
          case UpdateCleaning(_, cleaning)       => updateCleaning(cleaning)
          case ListMeasurements(_, poolId)       => listMeasurements(poolId)
          case AddMeasurement(_, measurement)    => addMeasurement(measurement)
          case UpdateMeasurement(_, measurement) => updateMeasurement(measurement)
          case ListChemicals(_, poolId)          => listChemicals(poolId)
          case AddChemical(_, chemical)          => addChemical(chemical)
          case UpdateChemical(_, chemical)       => updateChemical(chemical)

  private def isAuthorized(command: Command): Security =
    command match
      case license: License =>
        Try {
          if store.isAuthorized(license.license) then Authorized
          else Unauthorized(s"Unauthorized: $command")
        }.recover {
          case NonFatal(error) => Unauthorized(s"Unauthorized: $command, cause: $error")
        }.get
      case Register(_) | Login(_, _) => Authorized

  private def register(emailAddress: String): Event =
    Try {
      val account = Account(license = UUID.randomUUID.toString, emailAddress = emailAddress, pin = Pin.newInstance)
      if store.isEmailAddressUnique(emailAddress) then
        email(account.emailAddress, account.pin)
        Registered( store.register(account) )
      else Fault(s"Registration failed because: $emailAddress is already registered.")
    }.recover { case NonFatal(error) =>
      Fault(s"Registration failed for: $emailAddress with error: ${error.getMessage}")
    }.get

  private def email(emailAddress: String, pin: String): Unit =
    val recipients = List(emailAddress)
    val message = s"<p>Your new pin is: <b>${pin}</b></p><p>Welcome aboard!</p>"
    emailer.send(recipients, message)

  private def login(emailAddress: String, pin: String): Event =
    Try { store.login(emailAddress, pin) }.fold(
      error => Fault("Login failed:", error),
      optionalAccount =>
        if optionalAccount.isDefined then LoggedIn(optionalAccount.get)
        else Fault(s"Login failed for email address: $emailAddress and pin: $pin")
    )

  private def deactivateAccount(license: String): Event =
    Try { store.deactivateAccount(license) }.fold(
      error => Fault("Deactivate account failed:", error),
      optionalAccount =>
        if optionalAccount.isDefined then Deactivated(optionalAccount.get)
        else Fault(s"Deactivate account failed for license: $license")
    )

  private def reactivateAccount(license: String): Event =
    Try { store.reactivateAccount(license) }.fold(
      error => Fault("Reactivate account failed:", error),
      optionalAccount =>
        if optionalAccount.isDefined then Reactivated(optionalAccount.get)
        else Fault(s"Reactivate account failed for license: $license")
    )

  private def listPools(license: String): Event =
    Try {
      PoolsListed(store.listPools(license))
    }.recover { case NonFatal(error) => Fault("List pools failed:", error) }
     .get

  private def addPool(pool: Pool): Event =
    Try {
      PoolAdded( pool.copy(id = store.addPool(pool)) )
    }.recover { case NonFatal(error) => Fault("Add pool failed:", error) }
     .get

  private def updatePool(pool: Pool): Event =
    Try {
      Updated( store.updatePool(pool) )
    }.recover { case NonFatal(error) => Fault("Update pool failed:", error) }
     .get

  private def listCleanings(poolId: Long): Event =
    Try {
      CleaningsListed( store.listCleanings(poolId) )
    }.recover { case NonFatal(error) => Fault("List cleanings failed:", error) }
     .get

  private def addCleaning(cleaning: Cleaning): Event =
    Try {
      CleaningAdded( cleaning.copy(id = store.addCleaning(cleaning)) )
    }.recover { case NonFatal(error) => Fault("Add Cleaning failed:", error) }
     .get

  private def updateCleaning(cleaning: Cleaning): Event =
    Try {
      Updated( store.updateCleaning(cleaning) )
    }.recover { case NonFatal(error) => Fault(s"Update cleaning failed:", error) }
     .get
    
  private def listMeasurements(poolId: Long): Event =
    Try {
      MeasurementsListed( store.listMeasurements(poolId) )
    }.recover { case NonFatal(error) => Fault(s"List measurements failed:", error) }
     .get

  private def addMeasurement(measurement: Measurement): Event =
    Try {
      MeasurementAdded( measurement.copy(id = store.addMeasurement(measurement)) )
    }.recover { case NonFatal(error) => Fault("Add measurement failed:", error) }
     .get

  private def updateMeasurement(measurement: Measurement): Event =
    Try {
      Updated( store.updateMeasurement(measurement) )
    }.recover { case NonFatal(error) => Fault("Update measurement failed:", error) }
     .get
    
  private def listChemicals(poolId: Long): Event =
    Try {
      ChemicalsListed( store.listChemicals(poolId) )
    }.recover { case NonFatal(error) => Fault("List chemicals failed:", error) }
     .get

  private def addChemical(chemical: Chemical): Event =
    Try {
      ChemicalAdded( chemical.copy(id = store.addChemical(chemical)) )
    }.recover { case NonFatal(error) => Fault("Add chemical failed:", error) }
     .get

  private def updateChemical(chemical: Chemical): Event =
    Try {
      Updated( store.updateChemical(chemical) )
    }.recover { case NonFatal(error) => Fault("Update chemical failed:", error) }
     .get
