package workorder

import com.raquo.laminar.api.L.*

import org.scalajs.dom.console.log

import Components.*
import Validator.*

object WorkOrdersView extends View:
  def apply(): HtmlElement =
    def handler(result: Either[Fault, Event]): Unit =
      result match
        case Left(fault) => errorBus.emit(s"Refresh work orders failed: ${fault.cause}")
        case Right(event) =>
          event match
            case WorkOrdersListed(_, workOrders: List[WorkOrder], _, _) =>
              clearErrorBus()
              Model.workOrdersVar.set(workOrders)
            case _ => log("work orders view: handler failed: %o", event)

    def listWorkOrders(workOrders: Var[List[WorkOrder]]): Signal[List[Li]] =
      workOrders.signal.split(_.number)((number, _, workOrderSignal) =>
        item(workOrderSignal.map(wo => s"${wo.number} - ${wo.title}")).amend {
          onClick --> { _ =>
            workOrders.now().find(_.number == number).foreach { workOrder =>
              Model.workOrderVar.set(workOrder)
              Model.workOrderMode = if workOrder.closed.isEmpty then Mode.edit else Mode.readonly
              PageRouter.router.pushState(WorkOrderPage)
            }
          }
        }
      )

    div(
      bar(
        btn("Profile").amend {
          onClick --> { _ =>
            log("work orders view: profile menu item onClick")
            route(ProfilePage)
          }
        }      
      ),
      hdr("Work Orders"),
      lbl("Open"),
      div(
        list(listWorkOrders(Model.openWorkOrders))
      ),
      lbl("Closed"),
      div(
        list(listWorkOrders(Model.closedWorkOrders))
      ),
      cbar(
        btn("New").amend {
          disabled <-- Model.userVar.signal.map(user => user.role == Roles.serviceProvider)
          onClick --> { _ =>
            log("work orders view: new button onClick")
            Model.workOrderVar.set(WorkOrder.empty)
            route(WorkOrderPage)
          }
        }),
        btn("Refresh").amend {
          onClick --> { _ =>
            log("work orders view: refresh button onClick")
            val user = Model.userVar.now()
            val command = ListWorkOrders(user.id, user.license)
            call(command, handler)
          }
        }
      )