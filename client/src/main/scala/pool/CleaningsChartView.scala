package pool

import com.raquo.laminar.api.L.*

import Component.*

object CleaningsChartView extends View:
  def apply(model: Model[Chemical]): HtmlElement =
    div(
      hdr("Cleanings Chart")
    )