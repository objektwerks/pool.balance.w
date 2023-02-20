package pool

import com.raquo.laminar.api.L.*

import Component.*
import Validator.*

/* 
  totalChlorine: Int = 3,
  freeChlorine: Int = 3,
  combinedChlorine: Double = 0.0,
  ph: Double = 7.4,
  calciumHardness: Int = 375,
  totalAlkalinity: Int = 100,
  cyanuricAcid: Int = 50,
  totalBromine: Int = 5,
  salt: Int = 3200,
  temperature: Int = 85,
  measured: Long 
*/
object MeasurementView extends View:
  def apply(model: Model[Measurement], license: String): HtmlElement =
    def addHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case ChemicalAdded(chemical) =>
          clearErrors()
          model.addEntity(chemical)
          route(ChemicalsPage)
        case _ => log(s"Chemical -> add handler failed: $event")

    def updateHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case Updated(id) =>
          clearErrors()
          route(ChemicalsPage)
        case _ => log(s"Chemicals -> update handler failed: $event")

    div(
      bar(
        btn("Chemicals").amend {
          onClick --> { _ =>
            log("Chemical -> Chemicals menu item onClick")
            route(ChemicalsPage)
          }
        }
      ),
      div(
        hdr("Chemical"),
        lbl("Type Of"),
        list( TypeOfChemical.toList ).amend {
          value <-- model.selectedEntityVar.signal.map(_.unit)
          onChange.mapToValue --> { value =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(typeof = value) )
          }
        },
        lbl("Amount"),
        dbl.amend {
          value <-- model.selectedEntityVar.signal.map(_.amount.toString)
          onInput.mapToValue.filter(_.toDoubleOption.nonEmpty).map(_.toDouble) --> { value =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(amount = value) )
          }
        },
        lbl("Unit"),
        list( UnitOfMeasure.toList ).amend {
          value <-- model.selectedEntityVar.signal.map(_.unit)
          onChange.mapToValue --> { value =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(unit = value) )
          }
        },
        lbl("Added"),
        rotxt.amend {
          value <-- model.selectedEntityVar.signal.map( chemical => LocalDate.ofEpochDay(chemical.added).toString )
        },
      ),
      cbar(
        btn("Add").amend {
          disabled <-- model.selectedEntityVar.signal.map { cleaning => cleaning.id.isGreaterThanZero }
          onClick --> { _ =>
            log(s"Chemical -> Add onClick")
            val command = AddChemical(license, model.selectedEntityVar.now())
            call(command, addHandler)

          }
        },
        btn("Update").amend {
          disabled <-- model.selectedEntityVar.signal.map { cleaning => cleaning.id.isZero }
          onClick --> { _ =>
            log(s"Chemical -> Update onClick")
            val command = UpdateChemical(license, model.selectedEntityVar.now())
            call(command, updateHandler)
          }
        }
      )
    )