package pool

import com.raquo.laminar.api.L.*

import Component.*

object Dashboard:
  def apply: HtmlElement =
    div(
      hdr("Dashboard"),
      ph
    )


  private def ph: Div =
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