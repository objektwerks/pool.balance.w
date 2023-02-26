package pool

import com.raquo.laminar.api.L.*

import java.text.NumberFormat

import org.scalajs.dom.console.log

import Entity.given
import Measurement.*

object Model:
  val emailAddressVar = Var("")
  val pinVar = Var("")
  val accountVar = Var(Account())

  val poolsModel = Model[Pool](Var(List.empty[Pool]), Var(Pool()), Pool(), poolOrdering)
  val cleaningsModel = Model[Cleaning](Var(List.empty[Cleaning]), Var(Cleaning()), Cleaning(), cleaningOrdering)
  val measurementsModel = Model[Measurement](Var(List.empty[Measurement]), Var(Measurement()), Measurement(), measurementOrdering)
  val chemicalsModel = Model[Chemical](Var(List.empty[Chemical]), Var(Chemical()), Chemical(), chemicalOrdering)

  measurementsModel
    .entitiesVar
    .toObservable
    .changes
    .collect( _ =>
      log(s"dashboard calculating ...")
      dashboard()
      log(s"dashboard calculated.")
    )

  val currentTotalChlorine = Var(0)
  val averageTotalChlorine = Var(0)
  def totalChlorineInRange(value: Int): Boolean = totalChlorineRange.contains(value)

  val currentFreeChlorine = Var(0)
  val averageFreeChlorine = Var(0)
  def freeChlorineInRange(value: Int): Boolean = freeChlorineRange.contains(value)

  val currentCombinedChlorine = Var(0.0)
  val averageCombinedChlorine = Var(0.0)
  def combinedChlorineInRange(value: Double): Boolean = combinedChlorineRange.contains(value)

  val currentPh = Var(0.0)
  val averagePh = Var(0.0)
  def phInRange(value: Double): Boolean = phRange.contains(value)

  val currentCalciumHardness = Var(0)
  val averageCalciumHardness = Var(0)
  def calciumHardnessInRange(value: Int): Boolean = calciumHardnessRange.contains(value)

  val currentTotalAlkalinity = Var(0)
  val averageTotalAlkalinity = Var(0)
  def totalAlkalinityInRange(value: Int): Boolean = totalAlkalinityRange.contains(value)

  val currentCyanuricAcid = Var(0)
  val averageCyanuricAcid = Var(0)
  def cyanuricAcidInRange(value: Int): Boolean = cyanuricAcidRange.contains(value)

  val currentTotalBromine = Var(0)
  val averageTotalBromine = Var(0)
  def totalBromineInRange(value: Int): Boolean = totalBromineRange.contains(value)

  val currentSalt = Var(0)
  val averageSalt = Var(0)
  def saltInRange(value: Int): Boolean = saltRange.contains(value)

  val currentTemperature = Var(0)
  val averageTemperature = Var(0)
  def temperatureInRange(value: Int): Boolean = temperatureRange.contains(value)

  private def dashboard(): Unit =
    val numberFormat = NumberFormat.getNumberInstance()
    numberFormat.setMaximumFractionDigits(1)
    measurementsModel.entitiesVar.now().foreach { measurement =>
      calculateCurrentMeasurements(measurement, numberFormat)
      calculateAverageMeasurements(numberFormat)
    }

  private def calculateCurrentMeasurements(measurement: Measurement, numberFormat: NumberFormat): Unit =
    currentTotalChlorine.set(measurement.totalChlorine)
    currentFreeChlorine.set(measurement.freeChlorine)
    currentCombinedChlorine.set(numberFormat.format( measurement.combinedChlorine ).toDouble)
    currentPh.set(numberFormat.format( measurement.ph ).toDouble)
    currentCalciumHardness.set(measurement.calciumHardness)
    currentTotalAlkalinity.set(measurement.totalAlkalinity)
    currentCyanuricAcid.set(measurement.cyanuricAcid)
    currentTotalBromine.set(measurement.totalBromine)
    currentSalt.set(measurement.salt)
    currentTemperature.set(measurement.temperature)

  private def calculateAverageMeasurements(numberFormat: NumberFormat): Unit =
    val measurements = measurementsModel.entitiesVar.now()
    val count = measurements.length
    averageTotalChlorine.set(measurements.map(_.totalChlorine).sum / count)
    averageFreeChlorine.set(measurements.map(_.freeChlorine).sum / count)
    averageCombinedChlorine.set(numberFormat.format( measurements.map(_.combinedChlorine).sum / count ).toDouble)
    averagePh.set(numberFormat.format( measurements.map(_.ph).sum / count ).toDouble)
    averageCalciumHardness.set(measurements.map(_.calciumHardness).sum / count)
    averageTotalAlkalinity.set(measurements.map(_.totalAlkalinity).sum / count)
    averageCyanuricAcid.set(measurements.map(_.cyanuricAcid).sum / count)
    averageTotalBromine.set(measurements.map(_.totalBromine).sum / count)
    averageSalt.set(measurements.map(_.salt).sum / count)
    averageTemperature.set(measurements.map(_.temperature).sum / count)

final case class Model[E <: Entity](entitiesVar: Var[List[E]],
                                    selectedEntityVar: Var[E],
                                    emptyEntity: E,
                                    ordering: Ordering[E]):
  given owner: Owner = new Owner {}
  entitiesVar.signal.foreach(entities => log(s"model entities -> ${entities.toString}"))
  selectedEntityVar.signal.foreach(entity => log(s"model selected entity -> ${entity.toString}"))

  def addEntity(entity: E): Unit = entitiesVar.update(_ :+ entity)

  def setEntities(entities: List[E]): Unit = entitiesVar.set(entities)

  def setSelectedEntityById(id: Long): Model[E] =
    selectedEntityVar.set(entitiesVar.now().find(_.id == id).getOrElse(emptyEntity))
    this

  def updateSelectedEntity(updatedSelectedEntity: E): Unit =
    entitiesVar.update { entities =>
      entities.map { entity =>
        if entity.id == updatedSelectedEntity.id then
          selectedEntityVar.set(updatedSelectedEntity)
          updatedSelectedEntity
        else entity
      }
    }

  def sort: Var[List[E]] =
    val sortedEntities = entitiesVar.now().sorted[E](ordering)
    entitiesVar.set(sortedEntities)
    entitiesVar