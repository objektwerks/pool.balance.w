package pool

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.typesafe.scalalogging.Logger

import io.helidon.webserver.http.{Handler => WebHandler, ServerRequest, ServerResponse}

final class Handler(dispatcher: Dispatcher,
                    store: Store,
                    logger: Logger) extends WebHandler:
  override def handle(request: ServerRequest,
                      response: ServerResponse): Unit =
    val commandJson = request.content.as(classOf[String])
    val command = readFromString[Command](commandJson)
    logger.debug(s"*** Handler command: $command")

    val event = dispatcher.dispatch(command)
    logger.debug(s"*** Handler event: $event")
    event match
      case fault @ Fault(_, _) =>
        logger.error(s"*** Handler fault: $fault")
        store.addFault(fault)
      case _ =>
    val eventJson = writeToString[Event](event)

    response
      .status(200)
      .header("Content-Type", "application/json; charset=UTF-8")
      .send(eventJson)