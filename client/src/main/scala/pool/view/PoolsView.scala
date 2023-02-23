package pool

import com.raquo.laminar.api.L.*

import Component.*

object PoolsView extends View:
  def apply(model: Model[Pool], license: String): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case PoolsListed(pools: List[Pool]) =>
          clearErrors()
          model.setEntities(pools)
        case _ => log(s"Pools -> handler failed: $event")

    div(
      bar(
        btn("App").amend {
          onClick --> { _ =>
            log("Pools -> App menu item onClick")
            route(AppPage)
          }
        }      
      ),
      div(
        onLoad --> { _ => 
          val command = ListPools(license)
          call(command, handler)
        },
        hdr("Pools"),
        err(errorBus),
        list(
          split(model.entitiesVar, (id: Long) => PoolPage(id))
        )
      ),
      cbar(
        btn("New").amend {
          onClick --> { _ =>
            log(s"Pools -> New button onClick")
            route(PoolPage())
          }
        },        
        btn("Refresh").amend {
          onClick --> { _ =>
            log(s"Pools -> Refresh button onClick")
            val command = ListPools(license)
            call(command, handler)
          }
        }
      )
    )