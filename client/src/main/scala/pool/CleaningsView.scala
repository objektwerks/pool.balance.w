package pool

import com.raquo.laminar.api.L.*

import Component.*

object CleaningsView extends View:
  def apply(poolId: Long, model: Model[Cleaning], license: String): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case CleaningsListed(cleanings: List[Cleaning]) =>
          clearErrors()
          model.setEntities(cleanings)
        case _ => emitError(s"Cleanings handler failed: $event")

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
          val command = ListCleanings(license, poolId)
          call(command, handler)
        },
        hdr("Cleanings"),
        err(errorBus),
        listview(
          split(model.entitiesVar, (id: Long) => CleaningPage(id))
        )
      ),
      cbar(
        btn("New").amend {
          onClick --> { _ =>
            log(s"Cleanings -> New button onClick")
            route(CleaningPage(poolId, model.selectedEntityVar.now().id))
          }
        },        
        btn("Refresh").amend {
          onClick --> { _ =>
            log(s"Cleanings -> Refresh button onClick")
            val command = ListCleanings(license, poolId)
            call(command, handler)
          }
        },
        btn("Chart").amend {
          onClick --> { _ =>
            log(s"Cleanings -> Chart button onClick")
            // route(CleaningsChartPage)
          }
        }
      )
    )