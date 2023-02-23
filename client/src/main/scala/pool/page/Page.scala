package pool.page

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
case object AccountPage extends Page
case object PoolsPage extends Page
case object CleaningsPage extends Page
case object MeasurementsPage extends Page
case object ChemicalsPage extends Page

sealed trait EntityPage extends Page:
  val id: Long
final case class PoolPage(id: Long = 0) extends EntityPage
final case class CleaningPage(id: Long = 0) extends EntityPage
final case class MeasurementPage(id: Long = 0) extends EntityPage
final case class ChemicalPage(id: Long = 0) extends EntityPage