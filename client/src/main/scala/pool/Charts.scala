package pool

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*

import com.raquo.laminar.api.L.{*, given}

import typings.chartJs.mod.*

/**
 * Chart.js, via ScalaJs, is not exactly ready
 * for prime time (2023.2.25). Try again later.
 */
object Charts:  
  def apply(): HtmlElement = ???