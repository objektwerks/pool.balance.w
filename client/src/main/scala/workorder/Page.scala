package workorder

import com.raquo.laminar.api.L.*

sealed trait Page:
  val title = "Work Order"

case object LoginRegisterPage extends Page
case object RegisterPage extends Page
case object LoginPage extends Page
case object ProfilePage extends Page
case object WorkOrdersPage extends Page
case object WorkOrderPage extends Page