package pool

import com.raquo.laminar.api.L.*

import Component.*

object MeasurementsView extends View:
  def apply(poolId: Long, model: Model[Measurement], license: String): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case MeasurementsListed(measurements: List[Measurement]) =>
          clearErrors()
          model.setEntities(measurements)
        case _ => emitError(s"Measurements handler failed: $event")

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
          val command = ListMeasurements(license, poolId)
          call(command, handler)
        },
        hdr("Measurements"),
        err(errorBus),
        listview(
          split(model.entitiesVar, (id: Long) => MeasurementPage(id))
        )
      ),
      cbar(
        btn("New").amend {
          onClick --> { _ =>
            log(s"Measurements -> New button onClick")
            route(MeasurementPage(poolId, model.selectedEntityVar.now().id))
          }
        },        
        btn("Refresh").amend {
          onClick --> { _ =>
            log(s"Measurements -> Refresh button onClick")
            val command = ListMeasurements(license, poolId)
            call(command, handler)
          }
        },
        btn("Chart").amend {
          onClick --> { _ =>
            log(s"Measurements -> Chart button onClick")
            route(MeasurementsChartPage)
          }
        }
      )
    )