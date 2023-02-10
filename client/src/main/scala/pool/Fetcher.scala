package pool

import com.raquo.laminar.api.L.*
import org.scalajs.dom
import org.scalajs.dom.BlobPart
import org.scalajs.dom.File
import org.scalajs.dom.FormData
import org.scalajs.dom.Headers
import org.scalajs.dom.HttpMethod
import org.scalajs.dom.RequestInit
import org.scalajs.dom.console.log

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps
import scala.scalajs.js
import scala.scalajs.js.Thenable.Implicits.*

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

import Serializer.given

object Fetcher:
  val jsonHeaders = new Headers {
    js.Array(
      js.Array("Content-Type", "application/json; charset=UTF-8"),
      js.Array("Accept", "application/json")
    )
  }
  val jsonParameters = new RequestInit {
    method = HttpMethod.POST
    headers = jsonHeaders
  }

  def now: Future[String] =
    ( for
        response <- dom.fetch(Urls.now)
        text <- response.text()
      yield text
    ).recover { case error: Exception => s"Now failed: ${error.getMessage}" }

  // Uncomment to enable client formdata support.
  def call(command: Command, handler: Either[Fault, Event] => Unit) =
    val event: Future[Event] = command match
      case Register(_, _, _, _) => post(command, jsonParameters, Urls.register)
      case Login(_, _) => post(command, jsonParameters, Urls.login)
    handle(event, handler)

  // Uncomment to enable client formdata support.
  private def post(command: Command, parameters: RequestInit, url: String): Future[Event] =
    parameters.body = write[Command](command)
    // command: FormData | Command
    // parameters.body = if command.isInstanceOf[FormData] then command.asInstanceOf[FormData] else write[Command](command.asInstanceOf[Command]) 
    log("fetcher:post command: %s url: %s parameters: %s", command, url, parameters)
    (
      for
        response <- dom.fetch(url, parameters)
        text <- response.text()
        event = read[Event](text)
      yield
        log("fetcher:post text: %s event: %o", text, event)
        event
      ).recover { case error: Exception =>
        log("fetcher:post failure: %s", error.getCause)
        Fault(error)
      }

  private def handle(future: Future[Event], handler: Either[Fault, Event] => Unit): Unit =
    future map { event =>
      handler(
        event match
          case fault: Fault =>
            log("fetcher:handle fault: %o", fault)
            Left(fault)
          case event: Event =>
            log("fetcher:handle event: %o", event)
            if event.success then Right(event) else Left(Fault(event.error))
      )
    }