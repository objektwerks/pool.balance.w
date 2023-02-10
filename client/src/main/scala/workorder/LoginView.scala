package workorder

import com.raquo.laminar.api.L.*

import org.scalajs.dom.console.log

import Components.*
import Validator.*

object LoginView extends View:
  def apply(): HtmlElement =
    val emailAddressVar = Var("")
    val pinVar = Var("")
    val emailAddressErrorBus = new EventBus[String]
    val pinErrorBus = new EventBus[String]

    def handler(result: Either[Fault, Event]): Unit =
      result match
        case Left(fault) => errorBus.emit(s"Login failed: ${fault.cause}")
        case Right(event) =>
          event match
            case LoggedIn(user, users, workOrders, _, _) =>
              clearErrorBus()
              log("login view: succeeded.")
              Model.userVar.set(user)
              Model.usersVar.set(users)
              Model.workOrdersVar.set(workOrders)
              route(WorkOrdersPage)
            case _ =>
              log("login view: failed: %o", event)
              errorBus.emit(s"Login failed: $event")
      
    div(      
      hdr("Login"),
      err(errorBus),
      lbl("Email Address"),
      email.amend {
        onInput.mapToValue.filter(_.nonEmpty).setAsValue --> emailAddressVar
        onKeyUp.mapToValue --> { value =>
          if value.isEmailAddress then clear(emailAddressErrorBus)
          else emit(emailAddressErrorBus, emailAddressInvalid)
        }
      },
      err(emailAddressErrorBus),
      lbl("Pin"),
      pin.amend {
        onInput.mapToValue.filter(_.nonEmpty).setAsValue --> pinVar
        onKeyUp.mapToValue --> { value =>
          if value.isPin then clear(pinErrorBus)
          else emit(pinErrorBus, pinInvalid)
        }
      },
      err(pinErrorBus),
      cbar(
        btn("Login").amend {
          disabled <-- emailAddressVar.signal.combineWithFn(pinVar.signal) {
            (email, pin) => !(email.isEmailAddress && pin.isPin)
          }
          onClick --> { _ =>
            val command = Login(emailAddressVar.now(), pinVar.now())
            log("login view: login button onClick command: %o", command)
            call(command, handler)
          }
        }
      )
    )