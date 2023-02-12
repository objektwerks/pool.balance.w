package pool

import com.raquo.laminar.api.L.*
import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

object Page:
  given JsonValueCodec[Page] = JsonCodecMaker.make[Page]( CodecMakerConfig.withDiscriminatorFieldName(None) )

sealed trait Page:
  val title = "Pool Balance"

case object HomePage extends Page
case object RegisterPage extends Page
case object LoginPage extends Page
case object AppPage extends Page