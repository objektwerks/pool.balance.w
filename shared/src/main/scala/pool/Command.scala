package pool

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

sealed trait Command

object Command:
  given JsonValueCodec[Command] = JsonCodecMaker.make[Command]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[License] = JsonCodecMaker.make[License]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[Register] = JsonCodecMaker.make[Register]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[Login] = JsonCodecMaker.make[Login]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[Deactivate] = JsonCodecMaker.make[Deactivate]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[Reactivate] = JsonCodecMaker.make[Reactivate]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[ListPools] = JsonCodecMaker.make[ListPools]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[AddPool] = JsonCodecMaker.make[AddPool]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[UpdatePool] = JsonCodecMaker.make[UpdatePool]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[ListCleanings] = JsonCodecMaker.make[ListCleanings]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[AddCleaning] = JsonCodecMaker.make[AddCleaning]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[UpdateCleaning] = JsonCodecMaker.make[UpdateCleaning]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[ListMeasurements] = JsonCodecMaker.make[ListMeasurements]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[AddMeasurement] = JsonCodecMaker.make[AddMeasurement]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[UpdateMeasurement] = JsonCodecMaker.make[UpdateMeasurement]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[ListChemicals] = JsonCodecMaker.make[ListChemicals]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[AddChemical] = JsonCodecMaker.make[AddChemical]( CodecMakerConfig.withDiscriminatorFieldName(None) )
  given JsonValueCodec[UpdateChemical] = JsonCodecMaker.make[UpdateChemical]( CodecMakerConfig.withDiscriminatorFieldName(None) )

sealed trait License:
  val license: String

final case class Register(emailAddress: String) extends Command
final case class Login(emailAddress: String, pin: String) extends Command

final case class Deactivate(license: String) extends Command with License
final case class Reactivate(license: String) extends Command with License

final case class ListPools(license: String) extends Command with License
final case class AddPool(license: String, pool: Pool) extends Command with License
final case class UpdatePool(license: String, pool: Pool) extends Command with License

final case class ListCleanings(license: String, poolId: Long) extends Command with License
final case class AddCleaning(license: String, cleaning: Cleaning) extends Command with License
final case class UpdateCleaning(license: String, cleaning: Cleaning) extends Command with License

final case class ListMeasurements(license: String, poolId: Long) extends Command with License
final case class AddMeasurement(license: String, measurement: Measurement) extends Command with License
final case class UpdateMeasurement(license: String, measurement: Measurement) extends Command with License

final case class ListChemicals(license: String, poolId: Long) extends Command with License
final case class AddChemical(license: String, chemical: Chemical) extends Command with License
final case class UpdateChemical(license: String, chemical: Chemical) extends Command with License