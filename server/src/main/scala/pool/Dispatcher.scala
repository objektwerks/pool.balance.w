package pool

import java.util.UUID

import ox.supervised
import ox.resilience.retry
import ox.scheduling.Schedule

import scala.concurrent.duration.*
import scala.util.Try
import scala.util.control.NonFatal

import Validator.*

final class Dispatcher(store: Store, emailer: Emailer):
  def dispatch(command: Command): Event =
    command.isValid match
      case false => addFault( Fault(s"Invalid command: $command") )
      case true =>
        isAuthorized(command) match
          case Unauthorized(cause) => addFault( Fault(cause) )
          case Authorized =>
            val event = command match
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
            event match
              case fault: Fault => addFault(fault)
              case _ => event

  private def isAuthorized(command: Command): Security =
    command match
      case license: License =>
        try
          supervised:
            retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )(
              if store.isAuthorized(license.license) then Authorized
              else Unauthorized(s"Unauthorized: $command")
            )
        catch
          case NonFatal(error) => Unauthorized(s"Unauthorized: $command, cause: $error")
      case Register(_) | Login(_, _) => Authorized

  private def sendEmail(emailAddress: String, message: String): Boolean =
    val recipients = List(emailAddress)
    emailer.send(recipients, message)

  private def register(emailAddress: String): Event =
    try
      supervised:
        val license = UUID.randomUUID().toString()
        val pin = Pin.newInstance
        val account = Account(license = license, emailAddress = emailAddress, pin = pin)
        val message = s"Your new pin is: ${account.pin}\n\nWelcome aboard!"
        val result = retry( Schedule.fixedInterval(600.millis).maxRepeats(1) )( sendEmail(account.emailAddress, message) )
        if result then
          Registered( store.register(account) )
        else
          throw IllegalArgumentException("Invalid email address.")
    catch
      case NonFatal(error) => Fault(s"Registration failed for: $emailAddress, because: ${error.getMessage}")

  private def login(emailAddress: String, pin: String): Event =
    Try:
      supervised:
        retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.login(emailAddress, pin) )
    .fold(
      error => Fault("Login failed:", error),
      optionalAccount =>
        if optionalAccount.isDefined then LoggedIn(optionalAccount.get)
        else Fault(s"Login failed for email address: $emailAddress and pin: $pin")
    )

  private def deactivateAccount(license: String): Event =
    Try:
      supervised:
        retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.deactivateAccount(license) )
    .fold(
      error => Fault("Deactivate account failed:", error),
      optionalAccount =>
        if optionalAccount.isDefined then Deactivated(optionalAccount.get)
        else Fault(s"Deactivate account failed for license: $license")
    )

  private def reactivateAccount(license: String): Event =
    Try:
      supervised:
        retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.reactivateAccount(license) )
    .fold(
      error => Fault("Reactivate account failed:", error),
      optionalAccount =>
        if optionalAccount.isDefined then Reactivated(optionalAccount.get)
        else Fault(s"Reactivate account failed for license: $license")
    )

  private def listPools(license: String): Event =
    try
      PoolsListed(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.listPools(license) )
      )
    catch
      case NonFatal(error) => Fault("List pools failed:", error)

  private def addPool(pool: Pool): Event =
    try
      PoolAdded(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( pool.copy(id = store.addPool(pool)) )
      )
    catch
      case NonFatal(error) => Fault("Add pool failed:", error)

  private def updatePool(pool: Pool): Event =
    try
      Updated(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.updatePool(pool) )
      )
    catch
      case NonFatal(error) => Fault("Update pool failed:", error)

  private def listCleanings(poolId: Long): Event =
    try
      CleaningsListed(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.listCleanings(poolId) )
      )
    catch
      case NonFatal(error) => Fault("List cleanings failed:", error)

  private def addCleaning(cleaning: Cleaning): Event =
    try
      CleaningAdded(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( cleaning.copy(id = store.addCleaning(cleaning)) )
      )
    catch
      case NonFatal(error) => Fault("Add Cleaning failed:", error)

  private def updateCleaning(cleaning: Cleaning): Event =
    try
      Updated(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.updateCleaning(cleaning) )
      )
    catch
      case NonFatal(error) => Fault(s"Update cleaning failed:", error)

  private def listMeasurements(poolId: Long): Event =
    try
      MeasurementsListed(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.listMeasurements(poolId) )
      )
    catch
      case NonFatal(error) => Fault("List measurements failed:", error)

  private def addMeasurement(measurement: Measurement): Event =
    try
      MeasurementAdded(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( measurement.copy(id = store.addMeasurement(measurement)) )
      )
    catch
      case NonFatal(error) => Fault("Add measurement failed:", error)

  private def updateMeasurement(measurement: Measurement): Event =
    try
      Updated(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.updateMeasurement(measurement) )
      )
    catch
      case NonFatal(error) => Fault("Update measurement failed:", error)

  private def listChemicals(poolId: Long): Event =
    try
      ChemicalsListed(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.listChemicals(poolId) )
      )
    catch
      case NonFatal(error) => Fault("List chemicals failed:", error)

  private def addChemical(chemical: Chemical): Event =
    try
      ChemicalAdded(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( chemical.copy(id = store.addChemical(chemical)) )
      )
    catch
      case NonFatal(error) => Fault("Add chemical failed:", error)

  private def updateChemical(chemical: Chemical): Event =
    try
      Updated(
        supervised:
          retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.updateChemical(chemical) )
      )
    catch
      case NonFatal(error) => Fault("Update chemical failed:", error)

  private def addFault(fault: Fault): Event =
    try
      supervised:
        retry( Schedule.fixedInterval(100.millis).maxRepeats(1) )( store.addFault(fault) )
    catch
      case NonFatal(error) => Fault("Add fault failed:", error)