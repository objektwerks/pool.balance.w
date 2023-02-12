package pool

import com.raquo.laminar.api.L.*

import org.scalajs.dom.console.log

object Model:
  val emailAddressVar = Var("")
  val pinVar = Var("")
  val accountVar = Var(Account.empty)

  val poolsModel = Model[Pool](Var(List.empty[Pool]), Var(Pool()), Pool())
  val cleaningsModel = Model[Cleaning](Var(List.empty[Cleaning]), Var(Cleaning()), Cleaning())
  val measurementsModel = Model[Measurement](Var(List.empty[Measurement]), Var(Measurement()), Measurement())
  val chemicalsModel = Model[Chemical](Var(List.empty[Chemical]), Var(Chemical()), Chemical())


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