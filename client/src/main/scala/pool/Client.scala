package pool

import com.raquo.laminar.api.L.*

import org.scalajs.dom.*

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("Client")
class Client(serverUrl: String) extends js.Object:
  Proxy.setServerUrl(serverUrl)

  render(
    container = document.getElementById("content"),
    rootNode = div( child <-- PageRouter.splitter.signal )
  )