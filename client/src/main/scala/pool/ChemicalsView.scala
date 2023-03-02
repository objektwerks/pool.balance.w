package pool

import com.raquo.laminar.api.L.*

import Component.*
import Validator.*

object ChemicalsView extends View:
  def apply(poolId: Long, model: Model[Chemical], license: String): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case ChemicalsListed(chemicals: List[Chemical]) =>
          clearErrors()
          model.setEntities(chemicals)
        case _ => log(s"Chemicals -> handler failed: $event")

    div(
      bar(
        btn("Pool").amend {
          onClick --> { _ =>
            log("Chemicals -> Pool menu item onClick")
            route(PoolPage(poolId))
          }
        }      
      ),
      div(
        onLoad --> { _ => 
          val command = ListChemicals(license, poolId)
          call(command, handler)
        },
        hdr("Chemicals"),
        err(errorBus),
        listview(
          split(model.entitiesVar, (id: Long) => ChemicalPage(id))
        )
      ),
      cbar(
        btn("New").amend {
          onClick --> { _ =>
            log(s"Chemicals -> New button onClick")
            route(ChemicalPage())
          }
        },        
        btn("Refresh").amend {
          onClick --> { _ =>
            log(s"Chemicals -> Refresh button onClick")
            val command = ListChemicals(license, poolId)
            call(command, handler)
          }
        }
      )
    )