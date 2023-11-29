package pool

import com.raquo.laminar.api.L.*

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import ChartBuilder.DataItem

/*
  salt: Int = 3200,
  temperature: Int = 85,
*/

object MeasurementsChart:
  private val dateFormat = DateTimeFormatter.ofPattern("M.dd")

  def buildTotalChlorineChart(model: Model[Measurement]): HtmlElement =
    val dataItems = model
      .entitiesVar
      .now()
      .map(m => DataItem( LocalDate.ofEpochDay(m.measured).format(dateFormat), m.totalChlorine ))
    ChartBuilder.build( Var(dataItems).signal )

  def buildFreeChlorineChart(model: Model[Measurement]): HtmlElement =
    val dataItems = model
      .entitiesVar
      .now()
      .map(m => DataItem( LocalDate.ofEpochDay(m.measured).format(dateFormat), m.freeChlorine ))
    ChartBuilder.build( Var(dataItems).signal )

  def buildCombinedChlorineChart(model: Model[Measurement]): HtmlElement =
    val dataItems = model
      .entitiesVar
      .now()
      .map(m => DataItem( LocalDate.ofEpochDay(m.measured).format(dateFormat), m.combinedChlorine ))
    ChartBuilder.build( Var(dataItems).signal )

  def buildPhChart(model: Model[Measurement]): HtmlElement =
    val dataItems = model
      .entitiesVar
      .now()
      .map(m => DataItem( LocalDate.ofEpochDay(m.measured).format(dateFormat), m.ph ))
    ChartBuilder.build( Var(dataItems).signal )

  def buildCalciumHardnessChart(model: Model[Measurement]): HtmlElement =
    val dataItems = model
      .entitiesVar
      .now()
      .map(m => DataItem( LocalDate.ofEpochDay(m.measured).format(dateFormat), m.calciumHardness ))
    ChartBuilder.build( Var(dataItems).signal )

  def buildTotalAlkalinityChart(model: Model[Measurement]): HtmlElement =
    val dataItems = model
      .entitiesVar
      .now()
      .map(m => DataItem( LocalDate.ofEpochDay(m.measured).format(dateFormat), m.totalAlkalinity ))
    ChartBuilder.build( Var(dataItems).signal )

  def buildCyanuricAcidChart(model: Model[Measurement]): HtmlElement =
    val dataItems = model
      .entitiesVar
      .now()
      .map(m => DataItem( LocalDate.ofEpochDay(m.measured).format(dateFormat), m.cyanuricAcid ))
    ChartBuilder.build( Var(dataItems).signal )

  def buildTotalBromineChart(model: Model[Measurement]): HtmlElement =
    val dataItems = model
      .entitiesVar
      .now()
      .map(m => DataItem( LocalDate.ofEpochDay(m.measured).format(dateFormat), m.totalBromine ))
    ChartBuilder.build( Var(dataItems).signal )

  def buildSaltChart(model: Model[Measurement]): HtmlElement =
    val dataItems = model
      .entitiesVar
      .now()
      .map(m => DataItem( LocalDate.ofEpochDay(m.measured).format(dateFormat), m.salt ))
    ChartBuilder.build( Var(dataItems).signal )