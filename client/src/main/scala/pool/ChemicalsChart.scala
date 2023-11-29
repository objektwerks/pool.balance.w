package pool

import com.raquo.laminar.api.L.*

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import ChartBuilder.DataItem

object ChemicalsChart:
  private val dateFormat = DateTimeFormatter.ofPattern("M.dd")

  private def build(model: Model[Chemical], chemical: String): HtmlElement =
    val dataItems = model
      .entitiesVar
      .now()
      .filter(c => c.chemical == chemical)
      .map(c => DataItem( LocalDate.ofEpochDay(c.added).format(dateFormat), c.amount ))
    ChartBuilder.build( Var(dataItems).signal )