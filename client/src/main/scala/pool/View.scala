package pool

import com.raquo.laminar.api.L.*

enum Mode:
  case add, edit, readonly

trait View:
  private[this] val errorBus = new EventBus[String]

  def call(command: Command, handler: Either[Fault, Event] => Unit): Unit = Proxy.call(command, handler)
  
  def route(page: Page): Unit = PageRouter.router.pushState(page)

  def emitError(message: String): Unit = errorBus.emit(message)

  def clearErrors(): Unit = errorBus.emit("")

  def emit(bus: EventBus[String], message: String): Unit = bus.emit(message)

  def clear(bus: EventBus[String]): Unit = bus.emit("")