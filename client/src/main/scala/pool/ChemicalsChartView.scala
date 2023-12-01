package pool

import com.raquo.laminar.api.L.*

import Component.*
import TypeOfChemical.*

object ChemicalsChartView extends View:
  private val chemicals = TypeOfChemical.toList
  private val chartBus = EventBus[HtmlElement]()

  private def buildChart(selected: String, model: Model[Chemical]): HtmlElement =
      TypeOfChemical.valueOf(selected) match
        case LiquidChlorine => ChemicalsChart.buildLiquidChlorineChart(model)
        case Trichlor => ChemicalsChart.buildTrichlorChart(model)
        case Dichlor => ChemicalsChart.buildDichlorChart(model)
        case CalciumHypochlorite => ChemicalsChart.buildCalciumHypochloriteChart(model)
        case Stabilizer => ChemicalsChart.buildStabilizerChart(model)
        case Algaecide => ChemicalsChart.buildAlgaecideChart(model)
        case MuriaticAcid => ChemicalsChart.buildMuriaticAcidChart(model)
        case Salt => ChemicalsChart.buildSaltChart(model)

  def apply(model: Model[Chemical]): HtmlElement =
    div(
      hdr("Chemicals Chart"),
      div(
        listbox(chemicals).amend(
          onChange.mapToValue --> { value => chartBus.emit( buildChart(value, model) ) }
        )
      ),
      div(
        child <-- chartBus.events
      ),
      cbar(
        btn("Chemicals").amend {
          onClick --> { _ =>
            log(s"Chemicals Chart -> Chemicals button onClick")
            route(ChemicalsPage)
          }
        }
      )
    )