package pool

import com.raquo.laminar.api.L.*

import org.scalajs.dom.console.log

import Measurement.*

object Model:
  val emailAddressVar = Var("")
  val pinVar = Var("")
  val accountVar = Var(Account())

  val poolsModel = Model[Pool](Var(List.empty[Pool]), Var(Pool()), Pool())
  val cleaningsModel = Model[Cleaning](Var(List.empty[Cleaning]), Var(Cleaning()), Cleaning())
  val measurementsModel = Model[Measurement](Var(List.empty[Measurement]), Var(Measurement()), Measurement())
  val chemicalsModel = Model[Chemical](Var(List.empty[Chemical]), Var(Chemical()), Chemical())

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

final case class Model[E <: Entity](entitiesVar: Var[List[E]],
                                    selectedEntityVar: Var[E],
                                    emptyEntity: E):
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