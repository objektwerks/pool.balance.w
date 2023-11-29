package pool

import com.raquo.laminar.api.L.*

import java.time.format.DateTimeFormatter

object MeasurementsChart:
  private val dateFormat = DateTimeFormatter.ofPattern("M.dd")

  def build(model: Model[Measurement]): HtmlElement = ???