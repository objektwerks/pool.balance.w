package pool

import com.raquo.laminar.api.L.*

import Component.*

object ChemicalsChartView extends View:
  private val chemicals = TypeOfChemical.toList
  private val selectedVar = Var(chemicals.head)

  def apply(model: Model[Chemical]): HtmlElement =
    div(
      hdr("Chemicals Chart"),
      div(
        listbox(chemicals).amend(
          onChange.mapToValue --> value.
        )
      ),
      div(idAttr("chemicals-chart-id")),
      cbar(
        btn("Chemicals").amend {
          onClick --> { _ =>
            log(s"Chemicals Chart -> Chemicals button onClick")
            route(ChemicalsPage)
          }
        }
      )
    )