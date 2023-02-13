package pool

import com.raquo.laminar.api.L.*

import org.scalajs.dom.console.log

import Component.*
import Error.*
import Validator.*

object PoolView extends View:
  def apply(model: Model[Pool], accountVar: Var[Account]): HtmlElement =
    val nameErrorBus = new EventBus[String]
    val volumeErrorBus = new EventBus[String]

    def handler(either: Either[Fault, Event]): Unit =
      either match
        case Left(fault) => emitError(s"Add pool failed: ${fault.cause}")
        case Right(event) =>
          event match
            case PoolSaved(id) =>
              clearErrors()
              // model.addEntity(id) added or updated?
              route(PoolsPage)
            case _ => log(s"Pool -> add handler failed: $event")

    div(
      bar(
        btn("Pools").amend {
          onClick --> { _ =>
            log("Pool -> Pools menu item onClick")
            route(PoolsPage)
          }
        } // TODO - Cleanings, Measurements, Chemicals
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
        txt.amend {
          value <-- model.selectedEntityVar.signal.map(_.unit)
          // TODO! Requires a select dropdown.
        },
      ),
      cbar(
        btn("Save").amend {
          disabled <-- model.selectedEntityVar.signal.map { pool => pool.id.isGreaterThanZero }
          onClick --> { _ =>
            log(s"Pool -> Save onClick")
            val command = SavePool(accountVar.now().license, model.selectedEntityVar.now())
            call(command, handler)
          }
        }
      )
    )