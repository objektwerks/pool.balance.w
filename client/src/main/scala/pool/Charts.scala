package pool

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*

import com.raquo.laminar.api.L.{*, given}

import typings.chartJs.mod.*

object Charts:
  def apply(): HtmlElement =
    canvasTag (
      width := "100%",
      height := "300px"

    )