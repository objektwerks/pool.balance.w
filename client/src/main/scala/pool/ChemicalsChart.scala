package pool

import com.raquo.laminar.api.L.*

import java.time.format.DateTimeFormatter

object ChemicalsChart:
  val dateFormat = DateTimeFormatter.ofPattern("M.dd")

  def build(model: Model[Chemical]): HtmlElement = ???