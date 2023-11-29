package pool

import com.raquo.laminar.api.L.*

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import ChartBuilder.DataItem

/*
  totalChlorine: Int = 3,
  freeChlorine: Int = 3,
  combinedChlorine: Double = 0.0,
  ph: Double = 7.4,
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