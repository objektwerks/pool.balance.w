package pool

import com.raquo.laminar.api.L.*

import Component.*

object CleaningsView extends View:
  def apply(model: Model[Cleaning], accountVar: Var[Account]): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case CleaningsListed(cleanings: List[Cleaning]) =>
          clearErrors()
          model.setEntities(cleanings)
        case _ => log(s"Cleanings -> handler failed: $event")

    div(
      bar(
        btn("Pool").amend {
          onClick --> { _ =>
            log("Cleanings -> Pool menu item onClick")
            route(PoolPage()) // pool id
          }
        }      
      ),
      div(
        onLoad --> { _ => 
          val command = ListCleanings(accountVar.now().license, 0) // pool id
          call(command, handler)
        },
        hdr("Cleanings"),
        list(
          split(model.entitiesVar, (id: Long) => CleaningPage(id))
        )
      ),
      cbar(
        btn("New").amend {
          onClick --> { _ =>
            log(s"Cleanings -> New button onClick")
            route(CleaningPage())
          }
        },        
        btn("Refresh").amend {
          onClick --> { _ =>
            log(s"Cleanings -> Refresh button onClick")
            val command = ListCleanings(accountVar.now().license, 0) // pool id
            call(command, handler)
          }
        }
      )
    )