package pool

import com.raquo.laminar.api.L.*

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import ChartBuilder.DataItem

/*
enum TypeOfChemical(val display: String):
  case LiquidChlorine extends TypeOfChemical("Liquid Chlorine")
  case Trichlor extends TypeOfChemical("Trichlor")
  case Dichlor extends TypeOfChemical("Dichlor")
  case CalciumHypochlorite extends TypeOfChemical("Calcium Hypochlorite")
  case Stabilizer extends TypeOfChemical("Stabilizer")
  case Algaecide extends TypeOfChemical("Algaecide")
  case MuriaticAcid extends TypeOfChemical("Muriatic Acid")
  case Salt extends TypeOfChemical("Salt")
*/

object ChemicalsChart:
  private val dateFormat = DateTimeFormatter.ofPattern("M.dd")

  private def build(model: Model[Chemical], chemical: TypeOfChemical): HtmlElement =
    val dataItems = model
      .entitiesVar
      .now()
      .filter(c => c.chemical == chemical.toString)
      .map(c => DataItem( LocalDate.ofEpochDay(c.added).format(dateFormat), c.amount ))
    ChartBuilder.build( Var(dataItems).signal )

  def buildLiquidChlorineChart(model: Model[Chemical]): HtmlElement = build(model, TypeOfChemical.LiquidChlorine)

  def buildTrichlorChart(model: Model[Chemical]): HtmlElement = build(model, TypeOfChemical.Trichlor)

  def buildDichlorChart(model: Model[Chemical]): HtmlElement = build(model, TypeOfChemical.Dichlor)

  def buildCalciumHypochloriteChart(model: Model[Chemical]): HtmlElement = build(model, TypeOfChemical.CalciumHypochlorite)

  def buildStabilizerChart(model: Model[Chemical]): HtmlElement = build(model, TypeOfChemical.Stabilizer)

  def buildAlgaecideChart(model: Model[Chemical]): HtmlElement = build(model, TypeOfChemical.Algaecide)

  def buildMuriaticAcidChart(model: Model[Chemical]): HtmlElement = build(model, TypeOfChemical.MuriaticAcid)
