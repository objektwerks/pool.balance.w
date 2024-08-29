package pool

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import io.helidon.cors.CrossOriginConfig
import io.helidon.webserver.WebServer
import io.helidon.webserver.cors.CorsSupport
import io.helidon.webserver.http.HttpRouting

import ox.{ExitCode, IO, Ox, OxApp}

object Server extends OxApp with LazyLogging:
  override def run(args: Vector[String])(using Ox, IO): ExitCode =
    val config = ConfigFactory.load("server.conf")
    val host = config.getString("server.host")
    val port = config.getInt("server.port")
    val endpoint = config.getString("server.endpoint")

    val store = Store(config)
    val emailer = Emailer(config)
    val dispatcher = Dispatcher(store, emailer)
    val cors = CorsSupport
      .builder()
      .addCrossOrigin(
        CrossOriginConfig
          .builder()
          .allowCredentials(true)
          .allowHeaders("*")
          .allowMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
          .allowOrigins("http://localhost")
          .build()
      )
      .addCrossOrigin(CrossOriginConfig.create())
      .build()
    val handler = Handler(dispatcher)
    val builder = HttpRouting
      .builder
      .get(endpoint, cors, handler)

    WebServer
      .builder
      .port(port)
      .routing(builder)
      .build
      .start

    println(s"*** Press Control-C to shutdown Pool Balance Http Server at: $host:$port")
    logger.info(s"*** Pool Balance Http Server started at: $host:$port")

    Thread.currentThread().join()

    ExitCode.Success
