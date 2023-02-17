package pool

import com.typesafe.scalalogging.LazyLogging

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.sun.net.httpserver.{HttpExchange, HttpHandler}

import scala.io.{Codec, Source}

import Serializer.given

final class CommandHandler(dispatcher: Dispatcher, store: Store) extends HttpHandler with LazyLogging:
  override def handle(exchange: HttpExchange): Unit =
    val json = Source.fromInputStream( exchange.getRequestBody )(Codec.UTF8).mkString("")
    val command = readFromString[Command](json)
    
    val event = dispatcher.dispatch(command)
    event match
      case fault @ Fault(cause, _) =>
        logger.error(cause)
        store.addFault(fault)
      case _ =>
    val response = writeToString[Event](event)

    exchange.sendResponseHeaders(200, response.length())
    exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8")

    val outputStream = exchange.getResponseBody
    outputStream.write(response.getBytes())
    outputStream.flush()
    outputStream.close()