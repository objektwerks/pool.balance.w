package pool

import java.time.Instant

sealed trait Event

final case class Registered(account: Account) extends Event
final case class LoggedIn(account: Account) extends Event

final case class Deactivated(account: Account) extends Event
final case class Reactivated(account: Account) extends Event

final case class Updated(id: Long) extends Event

final case class PoolsListed(pools: List[Pool]) extends Event
final case class PoolAdded(pool: Pool) extends Event

final case class CleaningsListed(cleanings: List[Cleaning]) extends Event
final case class CleaningAdded(cleaning: Cleaning) extends Event

final case class MeasurementsListed(measurements: List[Measurement]) extends Event
final case class MeasurementAdded(measurement: Measurement) extends Event

final case class ChemicalsListed(chemicals: List[Chemical]) extends Event
final case class ChemicalAdded(chemical: Chemical) extends Event

object Fault:
  def apply(message: String, throwable: Throwable): Fault = Fault(s"$message ${throwable.getMessage}")

final case class Fault(cause: String, occurred: Long = Instant.now.toEpochMilli) extends Event