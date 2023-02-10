package workorder

import com.raquo.laminar.api.L.*

import org.scalajs.dom.console.log

import Components.*
import Validator.*

object ProfileView extends View:
  def apply(): HtmlElement =
    val emailAddressErrorBus = new EventBus[String]
    val streetAddressErrorBus = new EventBus[String]

    def handler(result: Either[Fault, Event]): Unit =
      result match
        case Left(fault) => errorBus.emit(s"Save profile failed: ${fault.cause}")
        case Right(event) =>
          event match
            case UserSaved(_, _, _) =>
              clearErrorBus()
              log("profile view: succeeded.")
              route(WorkOrdersPage)
            case _ => log("profile view: failed: %o", event)

    div(
      bar(
        btn("Work Orders").amend {
          onClick --> { _ => route(WorkOrdersPage) }
        }      
      ),
      div(
        hdr("Profile"),
        err(errorBus),
        lbl("Role"),
        rotxt.amend {
          value <-- Model.userVar.signal.map(_.role)
        },
        lbl("Name"),
        rotxt.amend {
          value <-- Model.userVar.signal.map(_.name)
        },
        lbl("Email Address"),
        email.amend {
          onInput.mapToValue.filter(_.nonEmpty) --> { value =>
            Model.userVar.update(user => user.copy(emailAddress = value))
          }
          onKeyUp.mapToValue --> { value =>
            if value.isEmailAddress then clear(emailAddressErrorBus)
            else emit(emailAddressErrorBus, emailAddressInvalid)
          }
        },
        err(emailAddressErrorBus),
        lbl("Street Address"),
        street.amend {
          onInput.mapToValue.filter(_.nonEmpty) --> { value =>
            Model.userVar.update(user => user.copy(streetAddress = value))
          }
          onKeyUp.mapToValue --> { value =>
            if value.isStreetAddress then clear(streetAddressErrorBus)
            else emit(streetAddressErrorBus, streetAddressInvalid)
          }
        },
        err(streetAddressErrorBus),
        cbar(
          btn("Save").amend {
            disabled <-- Model.userVar.signal.map { user => !user.isValid }
            onClick --> { _ =>
              val command = SaveUser(Model.userVar.now())
              log("profile view: save button onClick command: %o", command)
              call(command, handler)
            }
          } 
        )
      )
    )