package pool

import com.raquo.laminar.api.L.*

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
