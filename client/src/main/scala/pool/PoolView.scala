package pool

import com.raquo.laminar.api.L.*

import Component.*
import Error.*
import Validator.*

object PoolView extends View:
  def apply(model: Model[Pool], license: String): HtmlElement =
    val nameErrorBus = new EventBus[String]
    val volumeErrorBus = new EventBus[String]

    def addHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case PoolAdded(pool) =>
          clearErrors()
          model.addEntity(pool)
          route(PoolsPage)
        case _ => log(s"Pool -> add handler failed: $event")

    def updateHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case Updated(id) =>
          clearErrors()
          route(PoolsPage)
        case _ => log(s"Pool -> update handler failed: $event")

    div(
      bar(
        btn("Pools").amend {
          onClick --> { _ =>
            log("Pool -> Pools menu item onClick")
            route(PoolsPage)
          }
        },
        btn("Cleanings").amend {
          onClick --> { _ =>
            log("Pool -> Cleanings menu item onClick")
            route(PoolsPage)
          }
        },
        btn("Measurements").amend {
          onClick --> { _ =>
            log("Pool -> Measurements menu item onClick")
            route(PoolsPage)
          }
        },
        btn("Chemicals").amend {
          onClick --> { _ =>
            log("Pool -> Chemicals menu item onClick")
            route(PoolsPage)
          }
        },
      ),
      div(
        hdr("Pool"),
        lbl("Name"),
        txt.amend {
          value <-- model.selectedEntityVar.signal.map(_.name)
          onInput.mapToValue.filter(_.nonEmpty) --> { name =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(name = name) )
          }
          onKeyUp.mapToValue --> { name =>
            if name.isName then clear(nameErrorBus) else emit(nameErrorBus, nameError)
          }
        },
        err(nameErrorBus),
        lbl("Volume"),
        txt.amend {
          value <-- model.selectedEntityVar.signal.map(_.volume.toString)
          onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> { volume =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(volume = volume) )
          }
          onKeyUp.mapToValue.map(_.toInt) --> { volume =>
            if volume.isGreaterThan999 then clear(volumeErrorBus) else emit(volumeErrorBus, volumeError)
          }
        },
        err(volumeErrorBus),
        lbl("Unit"),
        list( UnitOfMeasure.toList ).amend {
          value <-- model.selectedEntityVar.signal.map(_.unit)
          onChange.mapToValue --> { value =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(unit = value) )
          }
        },
      ),
      cbar(
        btn("Add").amend {
          disabled <-- model.selectedEntityVar.signal.map { pool => pool.id.isGreaterThanZero }
          onClick --> { _ =>
            log(s"Pool -> Add onClick")
            val command = AddPool(license, model.selectedEntityVar.now())
            call(command, addHandler)

          }
        },
        btn("Update").amend {
          disabled <-- model.selectedEntityVar.signal.map { pool => pool.id.isZero }
          onClick --> { _ =>
            log(s"Pool -> Update onClick")
            val command = UpdatePool(license, model.selectedEntityVar.now())
            call(command, updateHandler)
          }
        }
      )
    )