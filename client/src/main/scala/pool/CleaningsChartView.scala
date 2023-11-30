package pool

import com.raquo.laminar.api.L.*

import Component.*

object CleaningsChartView extends View:
  def apply(model: Model[Cleaning]): HtmlElement =
    div(
      hdr("Cleanings Chart"),
      CleaningsChart.build(model),
      cbar(
        btn("Cleanings").amend {
          onClick --> { _ =>
            log(s"Cleanings Chart -> Cleanings button onClick")
            route(CleaningsPage)
          }
        }
      )
    )