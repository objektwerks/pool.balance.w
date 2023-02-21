package pool

import com.raquo.laminar.api.L.*

import java.time.LocalDate

import Component.*
import Validator.*

object CleaningView extends View:
  def apply(model: Model[Cleaning], license: String): HtmlElement =
    def addHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case CleaningAdded(cleaning) =>
          clearErrors()
          model.addEntity(cleaning)
          route(CleaningsPage)
        case _ => log(s"Cleaning -> add handler failed: $event")

    def updateHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case Updated(id) =>
          clearErrors()
          route(CleaningsPage)
        case _ => log(s"Cleanings -> update handler failed: $event")

    div(
      bar(
        btn("Cleanings").amend {
          onClick --> { _ =>
            log("Cleaning -> Cleanings menu item onClick")
            route(CleaningsPage)
          }
        }
      ),
      div(
        hdr("Cleaning"),
        err(errorBus),
        lbl("Brush"),
        checkbox.amend {
          value("Brush")
          controlled(
            checked <-- model.selectedEntityVar.signal.map(_.brush),
            onChange.mapToChecked --> { value =>
              model.selectedEntityVar.update(cleaning => cleaning.copy(brush = value))
            }
          )
        },
        lbl("Net"),
        checkbox.amend {
          value("Net")
          controlled(
            checked <-- model.selectedEntityVar.signal.map(_.net),
            onChange.mapToChecked --> { value =>
              model.selectedEntityVar.update(cleaning => cleaning.copy(net = value))
            }
          )
        },
        lbl("Skimmer Basket"),
        checkbox.amend {
          value("Skimmer Basket")
          controlled(
            checked <-- model.selectedEntityVar.signal.map(_.skimmerBasket),
            onChange.mapToChecked --> { value =>
              model.selectedEntityVar.update(cleaning => cleaning.copy(skimmerBasket = value))
            }
          )
        },
        lbl("Pump Basket"),
        checkbox.amend {
          value("Pump Basket")
          controlled(
            checked <-- model.selectedEntityVar.signal.map(_.pumpBasket),
            onChange.mapToChecked --> { value =>
              model.selectedEntityVar.update(cleaning => cleaning.copy(pumpBasket = value))
            }
          )
        },
        lbl("Pump Filter"),
        checkbox.amend {
          value("Pump Filter")
          controlled(
            checked <-- model.selectedEntityVar.signal.map(_.pumpFilter),
            onChange.mapToChecked --> { value =>
              model.selectedEntityVar.update(cleaning => cleaning.copy(pumpFilter = value))
            }
          )
        },
        lbl("Vacuum"),
        checkbox.amend {
          value("Vacuum")
          controlled(
            checked <-- model.selectedEntityVar.signal.map(_.vacuum),
            onChange.mapToChecked --> { value =>
              model.selectedEntityVar.update(cleaning => cleaning.copy(vacuum = value))
            }
          )
        },
        lbl("Cleaned"),
        rotxt.amend {
          value <-- model.selectedEntityVar.signal.map( cleaning => LocalDate.ofEpochDay(cleaning.cleaned).toString )
        },
      ),
      cbar(
        btn("Add").amend {
          disabled <-- model.selectedEntityVar.signal.map { cleaning => !(cleaning.id.isZero && cleaning.isValid) }
          onClick --> { _ =>
            log(s"Cleaning -> Add onClick")
            val command = AddCleaning(license, model.selectedEntityVar.now())
            call(command, addHandler)

          }
        },
        btn("Update").amend {
          disabled <-- model.selectedEntityVar.signal.map { cleaning => !(cleaning.id.isGreaterThanZero && cleaning.isValid) }
          onClick --> { _ =>
            log(s"Pool -> Update onClick")
            val command = UpdateCleaning(license, model.selectedEntityVar.now())
            call(command, updateHandler)
          }
        }
      )
    )