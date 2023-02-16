package pool

import scala.util.Try

import Serializer.given
import Validator.*
import scala.util.control.NonFatal

final class Dispatcher(store: Store,
                       emailer: Emailer):
  def dispatch[E <: Event](command: Command): Event =
    Try {
      if command.isValid && isAuthorized(command) then command match
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
      else Fault(s"Failed to process invalid command: $command")
    }.recover {
      case NonFatal(error) => Fault(s"Failed to process command: $command, because: ${error.getMessage}")
    }.get

  private val subject = "Account Registration"

  private def isAuthorized(command: Command): Boolean =
    command match
      case license: License          => store.isAuthorized(license.license)
      case Register(_) | Login(_, _) => true

  private def register(emailAddress: String): Event =
    Try {
      val account = Account(license = Ids.newLicense, emailAddress = emailAddress, pin = Ids.newPin)
      if store.isEmailAddressUnique(emailAddress) then
        email(account.emailAddress, account.pin)
        Registered( store.register(account) )
      else Fault(s"Registration failed for: $emailAddress")
    }.recover { case NonFatal(error) => Fault(s"Registration failed for: $emailAddress, because: ${error.getMessage}") }
     .get


  private def email(emailAddress: String, pin: String): Boolean =
    val recipients = List(emailAddress)
    val message = s"<p>Save this pin: <b>${pin}</b> in a safe place; then delete this email.</p>"
    emailer.send(recipients, subject, message)

  private def login(emailAddress: String, pin: String): Event =
    val optionalAccount = store.login(emailAddress, pin)
    if optionalAccount.isDefined then LoggedIn(optionalAccount.get)
    else Fault(s"Failed to login due to invalid email address: $emailAddress and/or pin: $pin")

  private def deactivateAccount(license: String): Event =
    val optionalAccount = store.deactivateAccount(license)
    if optionalAccount.isDefined then Deactivated(optionalAccount.get)
    else Fault(s"Failed to deactivated account due to invalid license: $license")

  private def reactivateAccount(license: String): Event =
    val optionalAccount = store.reactivateAccount(license)
    if optionalAccount.isDefined then Reactivated(optionalAccount.get)
    else Fault(s"Failed to reactivate account due to invalid license: $license")

  private def listPools(license: String): Event =
    Try { PoolsListed(store.listPools(license)) }.recover { case NonFatal(error) => Fault(s"List pools failed: $error") }.get

  private def addPool(pool: Pool): Event =
    Try { PoolAdded( pool.copy(id = store.addPool(pool)) ) }.recover { case NonFatal(error) => Fault(s"Add pool failed: $error") }.get

  private def updatePool(pool: Pool): Event =
    Try { Updated( store.updatePool(pool) ) }.recover { case NonFatal(error) => Fault(s"Update pool failed: $error") }.get

  private def listCleanings(poolId: Long): Event =
    Try { CleaningsListed( store.listCleanings(poolId) ) }.recover { case NonFatal(error) => Fault(s"List cleanings failed: $error") }.get

  private def addCleaning(cleaning: Cleaning): Event =
    Try { CleaningAdded( cleaning.copy(id = store.addCleaning(cleaning)) ) }.recover { case NonFatal(error) => Fault(s"Add Cleaning failed: $error") }.get

  private def updateCleaning(cleaning: Cleaning): Event =
    Try { Updated( store.updateCleaning(cleaning) ) }.recover { case NonFatal(error) => Fault(s"Update cleaning failed: $error") }.get
    
  private def listMeasurements(poolId: Long): Event = MeasurementsListed( store.listMeasurements(poolId) )

  private def addMeasurement(measurement: Measurement): Event = MeasurementAdded( measurement.copy(id = store.addMeasurement(measurement)) )

  private def updateMeasurement(measurement: Measurement): Event = Updated( store.updateMeasurement(measurement) )
    
  private def listChemicals(poolId: Long): Event = ChemicalsListed( store.listChemicals(poolId) )

  private def addChemical(chemical: Chemical): Event = ChemicalAdded( chemical.copy(id = store.addChemical(chemical)) )

  private def updateChemical(chemical: Chemical): Event = Updated( store.updateChemical(chemical) )