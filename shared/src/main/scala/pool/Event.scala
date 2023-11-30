package pool

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

import java.time.Instant

sealed trait Event

object Event:
  given JsonValueCodec[Event] = JsonCodecMaker.make[Event]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[Registered] = JsonCodecMaker.make[Registered]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[LoggedIn] = JsonCodecMaker.make[LoggedIn]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[Updated] = JsonCodecMaker.make[Updated]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[PoolsListed] = JsonCodecMaker.make[PoolsListed]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[PoolAdded] = JsonCodecMaker.make[PoolAdded]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[CleaningsListed] = JsonCodecMaker.make[CleaningsListed]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[CleaningAdded] = JsonCodecMaker.make[CleaningAdded]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[MeasurementsListed] = JsonCodecMaker.make[MeasurementsListed]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[MeasurementAdded] = JsonCodecMaker.make[MeasurementAdded]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[ChemicalsListed] = JsonCodecMaker.make[ChemicalsListed]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[ChemicalAdded] = JsonCodecMaker.make[ChemicalAdded]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[Fault] = JsonCodecMaker.make[Fault]( CodecMakerConfig.withDiscriminatorFieldName(None) )

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

final case class Fault(cause: String, occurred: String = Instant.now.toString) extends Event