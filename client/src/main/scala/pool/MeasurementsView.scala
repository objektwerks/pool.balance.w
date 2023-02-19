package pool

import com.raquo.laminar.api.L.*

import Component.*

object MeasurementsView extends View:
  def apply(model: Model[Measurement], accountVar: Var[Account]): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case MeasurementsListed(measurements: List[Measurement]) =>
          clearErrors()
          model.setEntities(measurements)
        case _ => log(s"Measurements -> handler failed: $event")

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
          val command = ListMeasurements(accountVar.now().license, 0) // pool id
          call(command, handler)
        },
        hdr("Measurements"),
        list(
          split(model.entitiesVar, (id: Long) => MeasurementPage(id))
        )
      ),
      cbar(
        btn("New").amend {
          onClick --> { _ =>
            log(s"Measurements -> New button onClick")
            route(MeasurementPage())
          }
        },        
        btn("Refresh").amend {
          onClick --> { _ =>
            log(s"Measurements -> Refresh button onClick")
            val command = ListMeasurements(accountVar.now().license, 0) // pool id
            call(command, handler)
          }
        }
      )
    )