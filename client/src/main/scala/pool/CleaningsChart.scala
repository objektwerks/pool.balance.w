package pool

import com.raquo.laminar.api.L.*

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import ChartBuilder.DataItem

object CleaningsChart:
  private val dateFormat = DateTimeFormatter.ofPattern("M.dd")

  private def cleaningsToInt(cleaning: Cleaning): Int =
    var count = 0
    if cleaning.brush then count += 1
    if cleaning.net then count += 1
    if cleaning.skimmerBasket then count += 1
    if cleaning.pumpBasket then count += 1
    if cleaning.pumpFilter then count += 1
    if cleaning.vacuum then count += 1
    count

  def build(model: Model[Cleaning]): HtmlElement =
    val dataItems = model
      .entitiesVar
      .now()
      .map(c => DataItem( LocalDate.ofEpochDay(c.cleaned).format(dateFormat), cleaningsToInt(c) ))
    ChartBuilder.build( Var(dataItems).signal )