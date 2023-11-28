package pool

import com.github.plokhotnyuk.jsoniter_scala.core.*

import org.scalajs.dom
import org.scalajs.dom.{Headers, HttpMethod, RequestInit}
import org.scalajs.dom.console.log

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.Thenable.Implicits.*
import scala.util.control.NonFatal

import Serializer.given

object Proxy:
  private var serverUrl = ""

  private val hdrs = new Headers {
    js.Array(
      js.Array("Content-Type", "application/json; charset=utf-8"),
      js.Array("Accept", "application/json")
    )
  }

  private val params = new RequestInit {
    method = HttpMethod.POST
    headers = hdrs
  }

  def setServerUrl(url: String): Unit =
    serverUrl = url
    log("Server url: %s", serverUrl)

  def call(command: Command,
           handler: (event: Event) => Unit) =
    val future = post(command)
    handle(future, handler)

  private def post(command: Command): Future[Event] =
    log(s"Proxy:post command: $command")
    params.body = writeToString[Command](command)
    log(s"Proxy:post params: $params")
    (
      for
        response <- dom.fetch(serverUrl, params)
        json     <- response.text()
      yield
        log(s"Proxy:post json: $json")
        readFromString[Event](json)
    ).recover {
      case NonFatal(failure) =>
        log(s"Proxy:post failure: ${failure.getCause}")
        Fault(failure.getMessage)
    }

  private def handle(future: Future[Event],
                     handler: (event: Event) => Unit): Unit =
    future map { event =>
      log(s"Proxy:handle event: $event")
      handler(event)
    }