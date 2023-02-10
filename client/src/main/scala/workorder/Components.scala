package workorder

import com.raquo.laminar.api.L.*

import scala.scalajs.js.Date

object Components:
  def bar(elms: HtmlElement*): Div =
    div(cls("w3-bar"), elms)

  def cbar(elms: HtmlElement*): Div =
    div(cls("w3-bar w3-margin-top w3-center"), elms)

  def btn(text: String): Button =
    button(cls("w3-button w3-round w3-indigo"), text)

  def rbtn(text: String): Button =
    button(cls("w3-button w3-round w3-indigo w3-right"), text)

  def checkbox: Input =
    input(cls("w3-input"), tpe("checkbox"))

  def lbl(text: String): Label =
    label(cls("w3-left-align w3-text-indigo"), text)

  def txt: Input =
    input(cls("w3-input w3-hover-light-gray w3-text-indigo"), required(true))

  def rotxt: Input =
    input(cls("w3-input w3-light-gray w3-text-indigo"), readOnly(true))

  def txtarea(rowCount: Int = 2): TextArea =
    textArea(cls("w3-hover-light-gray w3-text-indigo"), rows(rowCount))

  def rotxtarea(rowCount: Int = 2): TextArea =
    textArea(cls("w3-light-gray w3-text-indigo"), rows(rowCount), readOnly(true))

  def email: Input =
    input(cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("email"), minLength(3), required(true))

  def pin: Input =
    input(cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("text"), minLength(7), maxLength(7), required(true))

  def hdr(text: String): HtmlElement =
    h5(cls("w3-indigo"), text)

  def err(errBus: EventBus[String]): Div =
    div(cls("w3-container w3-border-white w3-text-red"), child.text <-- errBus.events)

  def street: Input =
    input(cls("w3-input w3-hover-light-gray w3-text-indigo"), minLength(7), required(true))

  def roles: Select =
    select(cls("w3-select w3-text-indigo"),
      option(Roles.homeowner, selected(true)),
      option(Roles.serviceProvider)
    )

  def list(items: List[String]): Select =
    select(cls("w3-select w3-text-indigo"),
      children <-- Var(items.map(item => option(item))).signal
    )

  def list(items: Signal[List[Li]]): HtmlElement =
    ul(cls("w3-ul w3-hoverable"), children <-- items)

  def item(stringSignal: Signal[String]): Li =
    li(cls("w3-text-indigo w3-display-container"), child.text <-- stringSignal)