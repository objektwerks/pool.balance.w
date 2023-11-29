package pool

import com.raquo.laminar.api.L.*

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import ChartBuilder.DataItem

/*
  calciumHardness: Int = 375,
  totalAlkalinity: Int = 100,
  cyanuricAcid: Int = 50,
  totalBromine: Int = 5,
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