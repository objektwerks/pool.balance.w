package pool

import com.raquo.laminar.api.L.*

import java.time.LocalDate

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
        case MeasurementAdded(measurement) =>
          clearErrors()
          model.addEntity(measurement)
          route(MeasurementsPage)
        case _ => log(s"Measurement -> add handler failed: $event")

    def updateHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case Updated(id) =>
          clearErrors()
          route(MeasurementsPage)
        case _ => log(s"Measurements -> update handler failed: $event")

    div(
      bar(
        btn("Measurements").amend {
          onClick --> { _ =>
            log("Measurement -> Measurements menu item onClick")
            route(MeasurementsPage)
          }
        }
      ),
      div(
        hdr("Measurement"),
        lbl("Total Chlorine"),
        int.amend {
          value <-- model.selectedEntityVar.signal.map(_.totalChlorine.toString)
          onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> { value =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(totalChlorine = value) )
          }
        },
        lbl("Free Chlorine"),
        int.amend {
          value <-- model.selectedEntityVar.signal.map(_.freeChlorine.toString)
          onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> { value =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(freeChlorine = value) )
          }
        },
        lbl("Combined Chlorine"),
        dbl.amend {
          value <-- model.selectedEntityVar.signal.map(_.combinedChlorine.toString)
          onInput.mapToValue.filter(_.toDoubleOption.nonEmpty).map(_.toDouble) --> { value =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(combinedChlorine = value) )
          }
        },
        lbl("Ph"), // TODO - use slider!
        dbl.amend {
          value <-- model.selectedEntityVar.signal.map(_.ph.toString)
          onInput.mapToValue.filter(_.toDoubleOption.nonEmpty).map(_.toDouble) --> { value =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(ph = value) )
          }
        },
        lbl("Calcium Hardness"),
        int.amend {
          value <-- model.selectedEntityVar.signal.map(_.calciumHardness.toString)
          onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> { value =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(calciumHardness = value) )
          }
        },
        lbl("Total Alkalinity"),
        int.amend {
          value <-- model.selectedEntityVar.signal.map(_.totalAlkalinity.toString)
          onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> { value =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(totalAlkalinity = value) )
          }
        },
        lbl("Cyanuric Acid"),
        int.amend {
          value <-- model.selectedEntityVar.signal.map(_.cyanuricAcid.toString)
          onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> { value =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(cyanuricAcid = value) )
          }
        },
        lbl("Measured"),
        rotxt.amend {
          value <-- model.selectedEntityVar.signal.map( measurement => LocalDate.ofEpochDay(measurement.measured).toString )
        },
      ),
      cbar(
        btn("Add").amend {
          disabled <-- model.selectedEntityVar.signal.map { cleaning => cleaning.id.isGreaterThanZero }
          onClick --> { _ =>
            log(s"Chemical -> Add onClick")
            val command = AddMeasurement(license, model.selectedEntityVar.now())
            call(command, addHandler)

          }
        },
        btn("Update").amend {
          disabled <-- model.selectedEntityVar.signal.map { cleaning => cleaning.id.isZero }
          onClick --> { _ =>
            log(s"Chemical -> Update onClick")
            val command = UpdateMeasurement(license, model.selectedEntityVar.now())
            call(command, updateHandler)
          }
        }
      )
    )