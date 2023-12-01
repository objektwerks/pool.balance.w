package pool

import com.raquo.laminar.api.L.*

import Component.*

object MeasurementsChartView extends View:
  private val chartBus = EventBus[HtmlElement]()

  private def buildChart(selected: String, model: Model[Measurement]): HtmlElement =
      selected match
        case "TotalChlorine" => MeasurementsChart.buildTotalChlorineChart(model)
        case "FreeChlorine" => MeasurementsChart.buildFreeChlorineChart(model)
        case "CombinedChlorine" => MeasurementsChart.buildCombinedChlorineChart(model)
        case "pH" => MeasurementsChart.buildPhChart(model)
        case "CalciumHardness" => MeasurementsChart.buildCalciumHardnessChart(model)
        case "TotalAlkalinity" => MeasurementsChart.buildTotalAlkalinityChart(model)
        case "CyanuricAcid" => MeasurementsChart.buildCyanuricAcidChart(model)
        case "TotalBromine" => MeasurementsChart.buildTotalBromineChart(model)
        case "Salt" => MeasurementsChart.buildSaltChart(model)
        case "Temperature" => MeasurementsChart.buildTemperatureChart(model)

  def apply(model: Model[Measurement]): HtmlElement =
    div(
      hdr("Measurements Chart"),
      div(
        listbox(Measurement.toList).amend(
          onChange.mapToValue --> { value => chartBus.emit( buildChart(value, model) ) }
        )
      ),
      div(
        child <-- chartBus.events
      ),
      cbar(
        btn("Measurements").amend {
          onClick --> { _ =>
            log(s"Measurements Chart -> Measurements button onClick")
            route(MeasurementsPage)
          }
        }
      )
    )