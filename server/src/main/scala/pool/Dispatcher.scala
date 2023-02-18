package pool

import com.typesafe.scalalogging.LazyLogging

import scala.util.Try
import scala.util.control.NonFatal

import Serializer.given
import Validator.*

final class Dispatcher(store: Store, emailer: Emailer) extends LazyLogging:
  def dispatch[E <: Event](command: Command): Event =
    if !command.isValid then Fault(s"Command is invalid: $command")
    isAuthorized(command) match
      case Authorized(isAuthorized) => if !isAuthorized then Fault(s"License is unauthorized: $command")
      case fault @ Fault(_, _) => fault
      case _ =>

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

  private def isAuthorized(command: Command): Event =
    command match
      case license: License =>
        Try {
          Authorized( store.isAuthorized(license.license) )
        }.recover { case NonFatal(error) => Fault(s"Authorization failed: $error") }
         .get
      case Register(_) | Login(_, _) => Authorized(true)

  private def register(emailAddress: String): Event =
    Try {
      val account = Account(license = Ids.newLicense, emailAddress = emailAddress, pin = Ids.newPin)
      if store.isEmailAddressUnique(emailAddress) then
        email(account.emailAddress, account.pin)
        Registered( store.register(account) )
      else Fault(s"Registration failed because: $emailAddress is already registered.")
    }.recover { case NonFatal(error) =>
      Fault(s"Registration failed for: $emailAddress, because: ${error.getMessage}")
    }.get

  private val subject = "Account Registration"

  private def email(emailAddress: String, pin: String): Unit =
    val recipients = List(emailAddress)
    val message = s"<p>Save this pin: <b>${pin}</b> in a safe place; then delete this email.</p>"
    emailer.send(recipients, subject, message)

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