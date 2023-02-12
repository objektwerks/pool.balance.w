package pool

import com.raquo.laminar.api.L.*

import org.scalajs.dom.console.log

import Component.*

object PoolsView extends View:
  def apply(model: Model[Pool], accountVar: Var[Account]): HtmlElement =
    def handler(either: Either[Fault, Event]): Unit =
      either match
        case Left(fault) => errorBus.emit(s"List pools failed: ${fault.cause}")
        case Right(event) =>
          event match
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
          val command = ListPools(accountVar.now().license)
          call(command, handler)
        },
        hdr("Pools"),
        list(
          // TODO split(poolsVar.entitiesVar, (id: Long) => PoolPage(id))
          List.empty[String]
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
            val command = ListPools(accountVar.now().license)
            call(command, handler)
          }
        }
      )
    )