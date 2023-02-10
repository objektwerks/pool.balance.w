package workorder

import com.raquo.laminar.api.L.*

enum Mode:
  case add, edit, readonly

trait View:
  protected[this] val errorBus = new EventBus[String]

  def call(command: Command, handler: Either[Fault, Event] => Unit): Unit = Fetcher.call(command, handler)
  
  def route(page: Page): Unit = PageRouter.router.pushState(page)

  def emit(bus: EventBus[String], message: String): Unit = bus.emit(message)

  def clear(bus: EventBus[String]): Unit = bus.emit("")

  def clearErrorBus(): Unit = errorBus.emit("")