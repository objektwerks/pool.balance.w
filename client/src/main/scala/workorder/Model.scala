package workorder

import com.raquo.laminar.api.L.*

import org.scalajs.dom.File

final case class ImageFile(number: Int, file: File, filename: String, url: String)

object Model:
  val userVar = Var(User.empty)
  val usersVar = Var(List.empty[User])
  val workOrdersVar = Var(List.empty[WorkOrder])
  val workOrderVar = Var(WorkOrder.empty)
  var workOrderMode = Mode.readonly
  val imageFile: Option[ImageFile] = None

  def addWorkOrder(workOrder: WorkOrder): Unit = workOrdersVar.update(_ :+ workOrder)

  def saveWorkOrder(workOrder: WorkOrder): Unit =
    removeWorkOrder(workOrder.number)
    addWorkOrder(workOrder)

  def updateWorkOrder(updatedWorkOrder: WorkOrder): Unit =
    workOrdersVar.update { workOrders =>
      workOrders.map { workOrder =>
        if workOrder.number == updatedWorkOrder.number then updatedWorkOrder
        else workOrder
      }
    }

  def removeWorkOrder(number: Int): Unit = workOrdersVar.update(_.filterNot(_.number == number))

  def userName(id: Int): String = usersVar.now().find(_.id == id).fold("")(_.name)

  def homeownersVar: Var[List[User]] = Var(usersVar.now().filter(user => user.role == Roles.homeowner))

  def serviceProvidersVar: Var[List[User]] = Var(usersVar.now().filter(user => user.role == Roles.serviceProvider))

  def openWorkOrders: Var[List[WorkOrder]] = Var(workOrdersVar.now().filter(workOrder => workOrder.closed.isEmpty))

  def closedWorkOrders: Var[List[WorkOrder]] = Var(workOrdersVar.now().filter(workOrder => workOrder.closed.nonEmpty))

  def imageFileUrl: String = imageFile.fold("")(_.url)