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
      hdr("Total Chlorine"),
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
      hdr("Free Chlorine"),
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
            value <-- Model.averageFreeChlorine.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.freeChlorineInRange(value.toInt) then inRangeAverage else outOfRangeAverage }
          }
        )
      )
    )

private object CombinedChlorinePane extends DashboardPane:
  def apply(): Div =
    div(
      hdr("Combined Chlorine"),
      grid(
        List(
          "Range:" -> lbl("0.0 - 0.5"),
          "Ideal:" -> lbl("0.0 - 0.2"),
          "Good:" -> lbl("0.0"),
          "Current:" -> current.amend("0.0").amend {
            value <-- Model.currentCombinedChlorine.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.combinedChlorineInRange(value.toDouble) then inRangeCurrent else outOfRangeCurrent }
          },
          "Average:" -> average.amend("0.0").amend {
            value <-- Model.averagePh.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.combinedChlorineInRange(value.toDouble) then inRangeAverage else outOfRangeAverage }
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
          "Current:" -> current.amend("0.0").amend {
            value <-- Model.currentPh.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.phInRange(value.toDouble) then inRangeCurrent else outOfRangeCurrent }
          },
          "Average:" -> average.amend("0.0").amend {
            value <-- Model.averagePh.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.phInRange(value.toDouble) then inRangeAverage else outOfRangeAverage }
          }
        )
      )
    )

private object CalciumHardnessPane extends DashboardPane:
  def apply(): Div =
    div(
      hdr("Calcium Hardness"),
      grid(
        List(
          "Range:" -> lbl("0 - 1000"),
          "Ideal:" -> lbl("250 - 500"),
          "Good:" -> lbl("375"),
          "Current:" -> current.amend("0").amend {
            value <-- Model.currentCalciumHardness.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.calciumHardnessInRange(value.toInt) then inRangeCurrent else outOfRangeCurrent }
          },
          "Average:" -> average.amend("0").amend {
            value <-- Model.averageCalciumHardness.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.calciumHardnessInRange(value.toInt) then inRangeAverage else outOfRangeAverage }
          }
        )
      )
    )

private object TotalAlkalinityPane extends DashboardPane:
  def apply(): Div =
    div(
      hdr("Total Alkalinity"),
      grid(
        List(
          "Range:" -> lbl("0 - 240"),
          "Ideal:" -> lbl("80 - 120"),
          "Good:" -> lbl("100"),
          "Current:" -> current.amend("0").amend {
            value <-- Model.currentTotalAlkalinity.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.totalAlkalinityInRange(value.toInt) then inRangeCurrent else outOfRangeCurrent }
          },
          "Average:" -> average.amend("0").amend {
            value <-- Model.averageTotalAlkalinity.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.totalAlkalinityInRange(value.toInt) then inRangeAverage else outOfRangeAverage }
          }
        )
      )
    )

private object CyanuricAcidPane extends DashboardPane:
  def apply(): Div =
    div(
      hdr("Cyanuric Acid"),
      grid(
        List(
          "Range:" -> lbl("0 - 300"),
          "Ideal:" -> lbl("30 - 100"),
          "Good:" -> lbl("50"),
          "Current:" -> current.amend("0").amend {
            value <-- Model.currentCyanuricAcid.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.cyanuricAcidInRange(value.toInt) then inRangeCurrent else outOfRangeCurrent }
          },
          "Average:" -> average.amend("0").amend {
            value <-- Model.averageCyanuricAcid.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.cyanuricAcidInRange(value.toInt) then inRangeAverage else outOfRangeAverage }
          }
        )
      )
    )

private object TotalBrominePane extends DashboardPane:
  def apply(): Div =
    div(
      hdr("Total Bromine"),
      grid(
        List(
          "Range:" -> lbl("0 - 20"),
          "Ideal:" -> lbl("2 - 10"),
          "Good:" -> lbl("5"),
          "Current:" -> current.amend("0").amend {
            value <-- Model.currentTotalBromine.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.totalBromineInRange(value.toInt) then inRangeCurrent else outOfRangeCurrent }
          },
          "Average:" -> average.amend("0").amend {
            value <-- Model.averageTotalBromine.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.totalBromineInRange(value.toInt) then inRangeAverage else outOfRangeAverage }
          }
        )
      )
    )

private object SaltPane extends DashboardPane:
  def apply(): Div =
    div(
      hdr("Salt"),
      grid(
        List(
          "Range:" -> lbl("0 - 3600"),
          "Ideal:" -> lbl("2700 - 3400"),
          "Good:" -> lbl("3200"),
          "Current:" -> current.amend("0").amend {
            value <-- Model.currentSalt.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.saltInRange(value.toInt) then inRangeCurrent else outOfRangeCurrent }
          },
          "Average:" -> average.amend("0").amend {
            value <-- Model.averageSalt.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.saltInRange(value.toInt) then inRangeAverage else outOfRangeAverage }
          }
        )
      )
    )

private object TemperaturePane extends DashboardPane:
  def apply(): Div =
    div(
      hdr("Temperature"),
      grid(
        List(
          "Range:" -> lbl("50 - 100"),
          "Ideal:" -> lbl("75 - 85"),
          "Good:" -> lbl("82"),
          "Current:" -> current.amend("0").amend {
            value <-- Model.currentTemperature.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.temperatureInRange(value.toInt) then inRangeCurrent else outOfRangeCurrent }
          },
          "Average:" -> average.amend("0").amend {
            value <-- Model.averageTemperature.signal.map(_.toString)
            onChange.mapToValue --> { value => if Model.temperatureInRange(value.toInt) then inRangeAverage else outOfRangeAverage }
          }
        )
      )
    )