package workorder

import com.raquo.laminar.api.L.*

import org.scalajs.dom.console.log

import Components.*
import Validator.*

object WorkOrderView extends View:
  def apply(): HtmlElement =
    val role = Model.userVar.now().role
    Model.workOrderMode match
      case Mode.add => add()
      case Mode.edit => edit(role)
      case Mode.readonly => readonly()

  def add(): HtmlElement =
    val titleErrorBus = new EventBus[String]
    val issueErrorBus = new EventBus[String]
    val streetAddressErrorBus = new EventBus[String]

    Model.workOrderVar.update(workOrder => workOrder.copy(homeownerId = Model.userVar.now().id))
    Model.workOrderVar.update(workOrder => workOrder.copy(opened = DateTime.now))

    def listServiceProviders(serviceProviders: Var[List[User]]): Signal[List[Li]] =
      serviceProviders.signal.split(_.id)((id, _, serviceProviderSignal) =>
        item(serviceProviderSignal.map(_.name)).amend {
          onClick --> { _ =>
            serviceProviders.now().find(_.id == id).foreach { serviceProvider =>
              Model.workOrderVar.update(workOrder => workOrder.copy(serviceProviderId = serviceProvider.id))
            }
          }
        }
      )

    div(
      bar(
        btn("Work Orders").amend {
          onClick --> { _ => route(WorkOrdersPage) }
        }      
      ),
      hdr("Work Order"),
      err(errorBus),
      lbl("Service Providers"),
      list( listServiceProviders(Model.serviceProvidersVar) ),
      lbl("Title"),
      txt.amend {
        onInput.mapToValue.filter(_.nonEmpty) --> { value =>
          Model.workOrderVar.update(workOrder => workOrder.copy(title = value))
        }
        onKeyUp.mapToValue --> { value =>
          if value.isTitle then clear(titleErrorBus)
          else emit(titleErrorBus, titleInvalid)
        }
      },
      err(titleErrorBus),
      lbl("Issue"),
      txtarea().amend {
        onInput.mapToValue.filter(_.nonEmpty) --> { value =>
          Model.workOrderVar.update(workOrder => workOrder.copy(issue = value))
        }
        onKeyUp.mapToValue --> { value =>
          if value.isIssue then clear(issueErrorBus)
          else emit(titleErrorBus, issueInvalid)
        }
      },
      err(issueErrorBus),
      lbl("Street Address"),
      street.amend {
        onInput.mapToValue.filter(_.nonEmpty) --> { value =>
          Model.workOrderVar.update(workOrder => workOrder.copy(streetAddress = value))
        }
        onKeyUp.mapToValue --> { value =>
          if value.isStreetAddress then clear(streetAddressErrorBus)
          else emit(streetAddressErrorBus, streetAddressInvalid)
        }
      },
      err(streetAddressErrorBus),
      cbar(
        btn("Add").amend {
          disabled <-- Model.workOrderVar.signal.map { workOrder => !workOrder.isValid }
          onClick --> { _ =>
            val command = AddWorkOrder(Model.workOrderVar.now(), Model.userVar.now().license)
            log("work order view: save button onClick command: %o", command)
            call(command, handler)
          }
        }
      )
    )

  def edit(role: String): HtmlElement =
    val titleErrorBus = new EventBus[String]
    val issueErrorBus = new EventBus[String]
    val streetAddressErrorBus = new EventBus[String]
    val resolutionErrorBus = new EventBus[String]
    
    div(
      bar(
        btn("Work Orders").amend {
          onClick --> { _ => route(WorkOrdersPage) }
        }      
      ),
      hdr("Work Order"),
      err(errorBus),
      lbl("Number"),
      rotxt.amend {
        value <-- Model.workOrderVar.signal.map(_.number.toString)
      },
      lbl("Homeowner"),
      rotxt.amend {
        value <-- Model.workOrderVar.signal.map(workOrder => Model.userName(workOrder.homeownerId))
      },
      lbl("Service Provider"),
      rotxt.amend {
        value <-- Model.workOrderVar.signal.map(workOrder => Model.userName(workOrder.serviceProviderId))
      },
      lbl("Title"),
      rotxt.amend {
        value <-- Model.workOrderVar.signal.map(_.title)
      },
      lbl("Issue"),
      txtarea().amend {
        readOnly( role == Roles.serviceProvider )
        onInput.mapToValue.filter(_.nonEmpty) --> { value =>
          Model.workOrderVar.update(workOrder => workOrder.copy(issue = value))
        }
        onKeyUp.mapToValue --> { value =>
          if value.isIssue then clear(issueErrorBus)
          else emit(titleErrorBus, issueInvalid)
        }
      },
      err(issueErrorBus),
      lbl("Street Address"),
      street.amend {
        readOnly( role == Roles.serviceProvider )
        onInput.mapToValue.filter(_.nonEmpty) --> { value =>
          Model.workOrderVar.update(workOrder => workOrder.copy(streetAddress = value))
        }
        onKeyUp.mapToValue --> { value =>
          if value.isStreetAddress then clear(streetAddressErrorBus)
          else emit(streetAddressErrorBus, streetAddressInvalid)
        }
      },
      err(streetAddressErrorBus),
      lbl("Resolution"),
      txtarea().amend {
        readOnly( role == Roles.homeowner )
        onInput.mapToValue.filter(_.nonEmpty) --> { value =>
          Model.workOrderVar.update(workOrder => workOrder.copy(resolution = value))
        }
        onKeyUp.mapToValue --> { value =>
          if value.isResolution then clear(resolutionErrorBus)
          else emit(resolutionErrorBus, resolutionInvalid)
        }
      },
      err(resolutionErrorBus),
      lbl("Opened"),
      rotxt.amend {
        value <-- Model.workOrderVar.signal.map(_.opened)
      },
      lbl("Closed"),
      checkbox.amend {
        value("Check to Close Work Order")
        readOnly( role == Roles.homeowner )
        checked <-- Model.workOrderVar.signal.map { _.closed.nonEmpty }
        onChange.mapToChecked --> { value =>
          val datetime = if value then DateTime.now else ""
          Model.workOrderVar.update(workOrder => workOrder.copy(closed = datetime))
        }
      },
      cbar(
        btn("Save").amend {
          disabled <-- Model.workOrderVar.signal.map { workOrder => !workOrder.isValid }
          onClick --> { _ =>
            val command = SaveWorkOrder(Model.workOrderVar.now(), Model.userVar.now().license)
            log("work order view: save button onClick command: %o", command)
            call(command, handler)
          }
        }
      )
    )

  def readonly(): HtmlElement =
    div(
      lbl("Number"),
      rotxt.amend {
        value <-- Model.workOrderVar.signal.map(_.number.toString)
      },
      lbl("Homeowner"),
      rotxt.amend {
        value <-- Model.workOrderVar.signal.map(workOrder => Model.userName(workOrder.homeownerId))
      },
      lbl("Service Provider"),
      rotxt.amend {
        value <-- Model.workOrderVar.signal.map(workOrder => Model.userName(workOrder.serviceProviderId))
      },
      lbl("Title"),
      rotxt.amend {
        value <-- Model.workOrderVar.signal.map(_.title)
      },
      lbl("Issue"),
      rotxtarea().amend {
        value <-- Model.workOrderVar.signal.map(_.issue)
      },
      lbl("Street Address"),
      rotxt.amend {
        value <-- Model.workOrderVar.signal.map(_.streetAddress)
      },
      lbl("Resolution"),
      rotxtarea().amend {
        value <-- Model.workOrderVar.signal.map(_.resolution)
      },
      lbl("Opened"),
      rotxt.amend {
        value <-- Model.workOrderVar.signal.map(_.opened)
      },
      lbl("Closed"),
      rotxt.amend {
        value <-- Model.workOrderVar.signal.map(_.closed)
      },
    )

  def handler(result: Either[Fault, Event]): Unit =
    result match
      case Left(fault) => errorBus.emit(s"Save work order failed: ${fault.cause}")
      case Right(event) =>
        event match
          case WorkOrderAdded(number, _, _) =>
            clearErrorBus()
            log("work order view: add succeeded.")
            Model.addWorkOrder(Model.workOrderVar.now().copy(number = number))
            route(WorkOrdersPage)
          case WorkOrderSaved(_, _, _) =>
            clearErrorBus()
            log("work order view: save succeeded.")
            Model.updateWorkOrder(Model.workOrderVar.now())
            route(WorkOrdersPage)
          case _ => log("work order view: handler failed: %o", event)
