package pool

import org.scalajs.dom._
import org.scalajs.dom.console.log

object Urls:
  var root = ""
  var now = ""
  var register = ""
  var login = ""

  def set(rootUrl: String): Unit =
    root = rootUrl
    now = s"$root/now"
    register = s"$root/register"
    login = s"$root/login"

    log("root url: %s", root)
    log("now url: %s", now)
    log("register url: %s", register)
    log("login url: %s", login)