package pool

import com.raquo.laminar.api.L.*

import org.scalajs.dom.console.log

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import Component.*
import Error.*
import Message.*
import Validator.*

object RegisterView extends View:
  def apply(emailAddressVar: Var[String], pinVar: Var[String], accountVar: Var[Account]): HtmlElement =
    val emailAddressErrorBus = new EventBus[String]

    def handler(either: Either[Fault, Event]): Unit =
      either match
        case Left(fault) => emitError(s"Register failed: ${fault.cause}")
        case Right(event) =>
          event match
            case Registered(account) =>
              clearErrors()
              accountVar.set(account)
              pinVar.set(account.pin)
              route(LoginPage)
            case _ => log(s"Register -> handler failed: $event")
      
    div(
      hdr("Register"),
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