package pool

import com.raquo.laminar.api.L.*

import scala.scalajs.js.Date

object Component:
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

  def info(text: String): Div =
    div(cls("w3-border-white w3-text-indigo"), b(text))

  def txt: Input =
    input(cls("w3-input w3-hover-light-gray w3-text-indigo"), required(true))

  def rotxt: Input =
    input(cls("w3-input w3-light-gray w3-text-indigo"), readOnly(true))

  def txtarea(rowCount: Int = 2): TextArea =
    textArea(cls("w3-hover-light-gray w3-text-indigo"), rows(rowCount))

  def rotxtarea(rowCount: Int = 2): TextArea =
    textArea(cls("w3-light-gray w3-text-indigo"), rows(rowCount), readOnly(true))

  def int: Input =
    input(typ("number"), pattern("\\d*"), stepAttr("1"), required(true))

  def dbl: Input =
    input(typ("number"), pattern("[0-9]+([.,][0-9]+)?"), stepAttr("0.01"), required(true))

  def email: Input =
    input(cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("email"), minLength(3), required(true))

  def pin: Input =
    input(cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("text"), minLength(7), maxLength(7), required(true))

  def hdr(text: String): HtmlElement =
    h5(cls("w3-indigo"), text)

  def err(errBus: EventBus[String]): Div =
    div(cls("w3-container w3-border-white w3-text-red"), child.text <-- errBus.events)

  def listbox(items: List[String]): Select =
    select(cls("w3-select w3-text-indigo"),
      children <-- Var(items.map(item => option(item))).signal
    )

  def listview(liSignal: Signal[List[LI]]): Div =
    div(cls("w3-container"), ul(cls("w3-ul w3-hoverable"), overflow("overflow: auto;"), children <-- liSignal))

  def item(strSignal: Signal[String]): LI =
    li(cls("w3-text-indigo w3-display-container"), child.text <-- strSignal)

  def split[E <: Entity](entities: Var[List[E]], toEntityPage: Long => EntityPage): Signal[List[LI]] =
    entities.signal.split(_.id)( (id, _, entitySignal) =>
      item( entitySignal.map(_.display) ).amend {
        onClick --> { _ =>
          entities.now().find(_.id == id).foreach { entity =>
            PageRouter.router.pushState(toEntityPage(id))
          }
        }
      }
    )

  def grid(labelElements: List[(String, HtmlElement)],
           leftColWidth: Int = 50,
           rightColWidth: Int = 50): Div =
    div(cls("w3-container"), styleAttr("padding: 6px"),
      labelElements.map { (label, element) =>
        div( cls("w3-row"),
          div( cls("w3-col"), styleAttr(s"width:$leftColWidth%"), lbl(label) ),
          div( cls("w3-col"), styleAttr(s"width:$rightColWidth%"), element )
        )
      }
    )

  def dashboard: HtmlElement = Dashboard()