package pool

import com.raquo.laminar.api.L.*

import Component.*

/*
  text = context.headerPh
  range.text = context.dashboardPhRange
  good.text = context.dashboardPhGood
  ideal.text = context.dashboardPhIdeal
  current.text <== model.currentPh.asString
  average.text <== model.averagePh.
  
  model.currentPh.onChange { (_, _, newValue) =>
    if model.phInRange(newValue) then inRangeCurrent else outOfRangeCurrent
  }

  model.averagePh.onChange { (_, _, newValue) =>
    if model.phInRange(newValue) then inRangeAverage else outOfRangeAverage
  }
*/
object Dashboard:
  def apply: HtmlElement = ???


final class PhPane:
  def apply: HtmlElement =

    div(
      hdr("Ph"),
      grid(
        List(
          "Range:" -> lbl("6.2 - 8.4"),
          "Good:" -> lbl("7.2 - 7.6"),
          "Ideal:" -> lbl("7.4"),
          "Current:" -> rotxt.amend { value <-- Model.currentPh.signal.map(_.toString) },
          "Average:" -> rotxt.amend { value <-- Model.averagePh.signal.map(_.toString) }
        )
      )
    )

