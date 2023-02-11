package pool

import com.raquo.laminar.api.L._

import org.scalajs.dom._
import org.scalajs.dom.console.log

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("Client")
class Client(serverUrl: String) extends js.Object:
  Urls.set(serverUrl)

  render(
    container = document.getElementById("content"),
    rootNode = div( child <-- PageRouter.splitter.signal )
  )