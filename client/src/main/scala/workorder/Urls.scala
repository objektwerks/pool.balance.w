package workorder

import org.scalajs.dom._
import org.scalajs.dom.console.log

object Urls:
  var root = ""
  var now = ""
  var register = ""
  var login = ""
  var userSave = ""
  var workOrderAdd = ""
  var workOrderSave = ""
  var workOrdersList = ""

  def set(rootUrl: String): Unit =
    root = rootUrl
    now = s"$root/now"
    register = s"$root/register"
    login = s"$root/login"
    userSave = s"$root/user/save"
    workOrderAdd = s"$root/workorder/add"
    workOrderSave = s"$root/workorder/save"
    workOrdersList = s"$root/workorders"

    log("root url: %s", root)
    log("now url: %s", now)
    log("register url: %s", register)
    log("login url: %s", login)
    log("userSave url: %s", userSave)
    log("workOrderAdd url: %s", workOrderAdd)
    log("workOrderSave url: %s", workOrderSave)
    log("workOrdersList url: %s", workOrdersList)
    log(s"server url: http://${window.location.host}")