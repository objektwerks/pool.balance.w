package workorder

import com.raquo.laminar.api.L.*

import org.scalajs.dom.console.log

import Components.*

object LoginRegisterView extends View:
  def apply(): HtmlElement =
    div(
      bar(
        btn("Login").amend {
          onClick --> { _ =>
            log("login register view: login menu item onClick")
            route(LoginPage)
          }
        },
        rbtn("Register").amend {
          onClick --> { _ =>
            log("login register view: register menu item onClick")
            route(RegisterPage)
          }
        }          
      )
    )