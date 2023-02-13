package pool

import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import java.net.InetSocketAddress
import java.util.concurrent.Executors

import scala.concurrent.duration.*

object Server extends LazyLogging:
  private val config = ConfigFactory.load("server.conf")
  private val host = config.getString("host")
  private val port = config.getInt("port")
  private val address = InetSocketAddress(port)
  private val backlog = 0
  private val handler = CommandHandler(dispatcher)
  private val filter = CorsFilter()

  private val store = Store(config, Store.cache(minSize = 4, maxSize = 10, expireAfter = 24.hour))
  private val emailer = Emailer(config)
  private val dispatcher = Dispatcher(store, emailer)

  private val http = HttpServer
    .create(
      address,
      backlog,
      "/command",
      handler,
      filter
    )

  @main def main(): Unit =
    http.setExecutor(Executors.newVirtualThreadPerTaskExecutor())
    http.start()

    println(s"*** Press Control-C to shutdown server at: $host:$port")
    logger.info(s"*** Http Server started at: $host:$port")

    sys.addShutdownHook {
      logger.info(s"*** Http Server shutting down at: $host:$port")
      http.stop(10)
    }

    Thread.currentThread().join()