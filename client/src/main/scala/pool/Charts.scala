package pool

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*

import com.raquo.laminar.api.L.{*, given}

import typings.chartJs.mod.*

object Charts:
  private val config = new ChartConfiguration {
    `type` = ChartType.line,
    data = new ChartData {
      datasets = js.Array(new ChartDataSets {
        label = "Value"
        borderWidth = 1
      }
  }
  
  def apply(): HtmlElement =
    canvasTag (
      width := "100%",
      height := "300px"

    )