package pool

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import java.net.InetSocketAddress
import java.util.concurrent.Executors

import scala.concurrent.duration.*
import scala.io.{Codec, Source}
import scala.util.Using

import Serializer.given

object Server extends LazyLogging:
  private val config = ConfigFactory.load("server.conf")
  private val host = config.getString("host")
  private val port = config.getInt("port")
  private val backlog = config.getInt("backlog")

  private val store = Store(config, Store.cache(minSize = 4, maxSize = 10, expireAfter = 24.hour))
  private val emailer = Emailer(config)
  private val dispatcher = Dispatcher(store, emailer)

  private val http = HttpServer.create(InetSocketAddress(port), backlog)
  private val handler = new HttpHandler {
    override def handle(exchange: HttpExchange): Unit =
      val json = Source.fromInputStream( exchange.getRequestBody )(Codec.UTF8).mkString("")
      val command = readFromString[Command](json)
      val event = dispatcher.dispatch(command)
      val response = writeToString[Event](event)

      exchange.sendResponseHeaders(200, response.length())
      exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8")

      val outputStream = exchange.getResponseBody
      outputStream.write(response.getBytes())
      outputStream.flush()
      outputStream.close()
  }

  @main def main(): Unit =
    http.setExecutor(Executors.newVirtualThreadPerTaskExecutor())
    http.createContext("/command", handler)
    http.start()

    println(s"*** Press Control-C to shutdown server at: $host:$port")
    logger.info(s"*** Http Server started at: $host:$port")

    sys.addShutdownHook {
      logger.info(s"*** Http Server shutting down at: $host:$port")
      http.stop(10)
    }

    Thread.currentThread().join()