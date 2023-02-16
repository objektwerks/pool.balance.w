package pool

import com.raquo.laminar.api.L.*

import org.scalajs.dom.console.log

import Component.*
import Validator.*

object AccountView extends View:
  def apply(accountVar: Var[Account]): HtmlElement =
    def deactivateHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case Deactivated(account) =>
          clearErrors()
          accountVar.set(account)
          route(AppPage)
        case _ => log(s"Account -> deactivate handler failed: $event")
 
    def reactivateHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case Reactivated(account) =>
          clearErrors()
          accountVar.set(account)
          route(AppPage)
        case _ => log(s"Account -> reactivate handler failed: $event")

    div(
      bar(
        btn("App").amend {
          onClick --> { _ =>
            log("Account -> App menu item onClick")
            route(AppPage)
          }
        }      
      ),
      div(
        hdr("Account"),
        lbl("License"),
        rotxt.amend {
          value <-- accountVar.signal.map(_.license)
        },
        lbl("Email Address"),
        rotxt.amend {
          value <-- accountVar.signal.map(_.emailAddress)
        },
        lbl("Pin"),
        rotxt.amend {
          value <-- accountVar.signal.map(_.pin)
        },
        lbl("Activated"),
        rotxt.amend {
          value <-- accountVar.signal.map(_.activated.toString)
        },
        lbl("Deactivated"),
        rotxt.amend {
          value <-- accountVar.signal.map(_.deactivated.toString)
        },
        cbar(
          btn("Deactivate").amend {
            disabled <-- accountVar.signal.map { account => account.isDeactivated }
            onClick --> { _ =>
              log("Account -> Deactivate button onClick")
              val command = Deactivate(accountVar.now().license)
              call(command, deactivateHandler)
            }
          },
          btn("Reactivate").amend {
            disabled <-- accountVar.signal.map { account => account.isActivated }
            onClick --> { _ =>
              log("Account -> Reactivate button onClick")
              val command = Reactivate(accountVar.now().license)
              call(command, reactivateHandler)
            }
          }      
        )
      )
    )