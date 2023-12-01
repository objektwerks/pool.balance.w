package pool

import com.raquo.laminar.api.L.*

import Component.*

object MeasurementsChartView extends View:
  private val chartBus = EventBus[HtmlElement]()

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