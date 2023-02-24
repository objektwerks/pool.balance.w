package pool

import com.raquo.laminar.api.L.*

import Component.*

object Dashboard:
  def apply: HtmlElement =
    div(
      hdr("Dashboard"),
      PhPane()
    )

sealed private trait DashboardPane:
  val current = lbl("0")
  val average = lbl("0")

  val redBorderStyle = "border-color: red; border-width: 3;"
  val emptyStyle = ""

  def inRangeCurrent: Unit = current.amend { styleAttr(emptyStyle) }
  def outOfRangeCurrent: Unit = current.amend { styleAttr(redBorderStyle) }

  def inRangeAverage: Unit = average.amend { styleAttr(emptyStyle) }
  def outOfRangeAverage: Unit = average.amend { styleAttr(redBorderStyle) }

private object TotalChlorinePane extends DashboardPane:
  def apply(): Div =
    div(
      hdr("Ph"),
      grid(
        List(
          "Range:" -> lbl("0 - 10"),
          "Ideal:" -> lbl("1 - 5"),
          "Good:" -> lbl("3"),
          "Current:" -> current.amend("0").amend {
            value <-- Model.currentTotalChlorine.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.totalChlorineInRange(value.toInt) then inRangeCurrent else outOfRangeCurrent }
          },
          "Average:" -> average.amend("0").amend {
            value <-- Model.averageTotalChlorine.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.totalChlorineInRange(value.toInt) then inRangeAverage else outOfRangeAverage }
          }
        )
      )
    )

private object FreeChlorinePane extends DashboardPane:
  def apply(): Div =
    div(
      hdr("Ph"),
      grid(
        List(
          "Range:" -> lbl("0 - 10"),
          "Ideal:" -> lbl("1 - 5"),
          "Good:" -> lbl("3"),
          "Current:" -> current.amend("0").amend {
            value <-- Model.currentFreeChlorine.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.freeChlorineInRange(value.toInt) then inRangeCurrent else outOfRangeCurrent }
          },
          "Average:" -> average.amend("0").amend {
            value <-- Model.averageTotalChlorine.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.totalChlorineInRange(value.toInt) then inRangeAverage else outOfRangeAverage }
          }
        )
      )
    )

private object PhPane extends DashboardPane:
  def apply(): Div =
    div(
      hdr("Ph"),
      grid(
        List(
          "Range:" -> lbl("6.2 - 8.4"),
          "Ideal:" -> lbl("7.4"),
          "Good:" -> lbl("7.2 - 7.6"),
          "Current:" -> current.amend("0").amend {
            value <-- Model.currentPh.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.phInRange(value.toDouble) then inRangeCurrent else outOfRangeCurrent }
          },
          "Average:" -> average.amend("0").amend {
            value <-- Model.averagePh.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.phInRange(value.toDouble) then inRangeAverage else outOfRangeAverage }
          }
        )
      )
    )