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
      case error: Throwable => Fault(s"Failed to process command: $command, due to this error: ${error.getMessage}")
    }.get

  private val subject = "Account Registration"

  private def isAuthorized(command: Command): Boolean =
    command match
      case license: License          => store.isAuthorized(license.license)
      case Register(_) | Login(_, _) => true

  private def register(emailAddress: String): Event =
    val account = Account(license = Ids.newLicense, emailAddress = emailAddress, pin = Ids.newPin)
    val sent = email(account.emailAddress, account.pin)
    val unique = store.isEmailAddressUnique(emailAddress)
    if sent && unique then Registered( store.register(account) )
    else Fault(s"Registration failed for: $emailAddress")

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
    Try { PoolsListed(store.listPools(license)) }.recover { case NonFatal(error) => Fault("List pools failed: $error") }.get

  private def addPool(pool: Pool): PoolAdded = PoolAdded( pool.copy(id = store.addPool(pool)) )

  private def updatePool(pool: Pool): Updated = Updated( store.updatePool(pool) )

  private def listCleanings(poolId: Long): CleaningsListed = CleaningsListed( store.listCleanings(poolId) )

  private def addCleaning(cleaning: Cleaning): CleaningAdded = CleaningAdded( cleaning.copy(id = store.addCleaning(cleaning)) )

  private def updateCleaning(cleaning: Cleaning): Updated = Updated( store.updateCleaning(cleaning) )
    
  private def listMeasurements(poolId: Long): MeasurementsListed = MeasurementsListed( store.listMeasurements(poolId) )

  private def addMeasurement(measurement: Measurement): MeasurementAdded = MeasurementAdded( measurement.copy(id = store.addMeasurement(measurement)) )

  private def updateMeasurement(measurement: Measurement): Updated = Updated( store.updateMeasurement(measurement) )
    
  private def listChemicals(poolId: Long): ChemicalsListed = ChemicalsListed( store.listChemicals(poolId) )

  private def addChemical(chemical: Chemical): ChemicalAdded = ChemicalAdded( chemical.copy(id = store.addChemical(chemical)) )

  private def updateChemical(chemical: Chemical): Updated = Updated( store.updateChemical(chemical) )