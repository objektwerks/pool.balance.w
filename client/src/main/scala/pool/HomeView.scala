package pool

import com.raquo.laminar.api.L.*

import Component.*

object HomeView extends View:
  def apply(): HtmlElement =
    div(
      bar(
        btn("Login").amend {
          onClick --> { _ =>
            log("Home -> Login menu item onClick")
            route(LoginPage)
          }
        },
        rbtn("Register").amend {
          onClick --> { _ =>
            log("Home -> Register menu item onClick")
            route(RegisterPage)
          }
        }          
      )
    )