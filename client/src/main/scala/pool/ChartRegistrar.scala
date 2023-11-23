package pool

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object ChartRegistrar:
  @js.native
  @JSImport("chart.js")
  object Chart extends js.Object:
    def register(components: js.Object*): Unit = js.native

  @js.native
  @JSImport("chart.js")
  object BarController extends js.Object

  @js.native
  @JSImport("chart.js")
  object CategoryScale extends js.Object

  @js.native
  @JSImport("chart.js")
  object LinearScale extends js.Object

  @js.native
  @JSImport("chart.js")
  object BarElement extends js.Object