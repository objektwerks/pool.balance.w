package pool

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  * Supports ChartType.bar, ChartType.line and ChartType.pie
  */
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
  object BarElement extends js.Object

  @js.native
  @JSImport("chart.js")
  object LineController extends js.Object

  @js.native
  @JSImport("chart.js")
  object LineElement extends js.Object

  @js.native
  @JSImport("chart.js")
  object PointElement extends js.Object

  @js.native
  @JSImport("chart.js")
  object PieController extends js.Object

  @js.native
  @JSImport("chart.js")
  object ArcElement extends js.Object

  @js.native
  @JSImport("chart.js")
  object CategoryScale extends js.Object

  @js.native
  @JSImport("chart.js")
  object LinearScale extends js.Object

  @js.native
  @JSImport("chart.js")
  object Legend extends js.Object