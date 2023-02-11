package pool

import org.scalajs.dom.console.log

object Urls:
  var server = ""

  def set(serverUrl: String): Unit =
    server = serverUrl
    log("server url: %s", server)
