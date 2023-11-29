package pool

import com.raquo.laminar.api.L.*

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import ChartBuilder.DataItem

object ChemicalsChart:
  val dateFormat = DateTimeFormatter.ofPattern("M.dd")

  def build(model: Model[Chemical]): HtmlElement =
    val dataItems = model
      .entitiesVar
      .now()
      .map(c => DataItem( LocalDate.ofEpochDay(c.added).format(dateFormat), c.amount ))
    ChartBuilder.build( Var(dataItems).signal )