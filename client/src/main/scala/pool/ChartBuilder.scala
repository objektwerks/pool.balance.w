package pool

import com.raquo.laminar.api.L.*

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*

import typings.chartJs.mod.*

import ChartRegistrar.{
  CategoryScale, LinearScale, LineController, LineElement, PointElement, Chart => ChartJs
}

object ChartBuilder:
  final case class DataItem(label: String, value: Double)

  ChartJs.register(
    CategoryScale, LinearScale, LineController, LineElement, PointElement
  )

  def build(dataSignal: StrictSignal[List[DataItem]]): HtmlElement =
    var optionalChart: Option[Chart] = None

    canvasTag(
      width := "100%",

      onMountUnmountCallback(
        mount = { mountContext =>
          val canvas = mountContext.thisNode.ref
          val chart = Chart.apply.newInstance2(canvas, new ChartConfiguration {
            `type` = ChartType.line
            data = new ChartData {
              datasets = js.Array(new ChartDataSets {
                label = "Value"
                borderWidth = 1
              })
            }
          })
          optionalChart = Some(chart)
        },
        unmount = { _ =>
          optionalChart.foreach(chart => chart.destroy())
          optionalChart = None
        }
      ),

      dataSignal --> { data =>
        optionalChart.foreach { chart =>
          chart.data.labels = data.map(_.label).toJSArray
          chart.data.datasets.get(0).data = data.map(_.value).toJSArray
          chart.update()
        }
      },
    )