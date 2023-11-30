package pool

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

object Serializer:
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