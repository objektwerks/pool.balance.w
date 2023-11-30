package pool

import com.raquo.laminar.api.L.*

import Component.*

object MeasurementsChartView extends View:
  def apply(model: Model[Chemical]): HtmlElement =
    div(
      hdr("Measurements Chart")
    )