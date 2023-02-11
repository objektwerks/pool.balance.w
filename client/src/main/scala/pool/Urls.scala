package pool

import org.scalajs.dom._
import org.scalajs.dom.console.log

object Urls:
  var root = ""
  var now = ""
  var command = ""

  def set(rootUrl: String): Unit =
    root = rootUrl
    now = s"$root/now"
    command = s"$root/command"

    log("root url: %s", root)
    log("now url: %s", now)
    log("command url: %s", command)
