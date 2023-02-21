package pool

import com.raquo.laminar.api.L.*

import Component.*
import Error.*
import Message.*
import Validator.*

object RegisterView extends View:
  def apply(emailAddressVar: Var[String], pinVar: Var[String], accountVar: Var[Account]): HtmlElement =
    val emailAddressErrorBus = new EventBus[String]

    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case Registered(account) =>
          clearErrors()
          accountVar.set(account)
          pinVar.set(account.pin)
          route(LoginPage)
        case _ => log(s"Register -> handler failed: $event")
      
    div(
      hdr("Register"),
      err(errorBus),
      info(registerMessage),
      lbl("Email Address"),
      email.amend {
        value <-- emailAddressVar
        onInput.mapToValue.filter(_.nonEmpty).setAsValue --> emailAddressVar
        onKeyUp.mapToValue --> { emailAddress =>
          if emailAddress.isEmailAddress then clear(emailAddressErrorBus) else emit(emailAddressErrorBus, emailAddressError)
        }
      },
      err(emailAddressErrorBus),
      cbar(
        btn("Register").amend {
          disabled <-- emailAddressVar.signal.map(email => !email.isEmailAddress)
          onClick --> { _ =>
            log(s"Register button onClick -> email address: ${emailAddressVar.now()}")
            val command = Register(emailAddressVar.now())
            call(command, handler)
          }
        },
      )
    )