package pool

import com.raquo.laminar.api.L.*

sealed trait Page:
  val title = "Pool Balance"

case object LoginRegisterPage extends Page