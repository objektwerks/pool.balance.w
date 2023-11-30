package pool

import com.raquo.laminar.api.L.*

import Component.*

object MeasurementsChartView extends View:
  def apply(model: Model[Measurement]): HtmlElement =
    div(
      hdr("Measurements Chart"),
      // TODO - build chart!
      cbar(
        btn("Measurements").amend {
          onClick --> { _ =>
            log(s"Measurements Chart -> Measurements button onClick")
            route(MeasurementsPage)
          }
        }
      )
    )