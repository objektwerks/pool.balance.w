package pool

import com.raquo.laminar.api.L.*

import Component.*
import Validator.*

object AppView extends View:
  def apply(accountVar: Var[Account]): HtmlElement =
    div(
      bar(
        btn("Account").amend {
          onClick --> { _ =>
            log("App -> Account menu item onClick")
            route(AccountPage)
          }
        },
        rbtn("Pools").amend {
          disabled <-- accountVar.signal.map { account => account.isDeactivated }
          onClick --> { _ =>
            log("App -> Pools menu item onClick")
            route(PoolsPage)
          }
        }          
      )
    )