package workorder

import com.raquo.laminar.api.L.*
import org.scalajs.dom
import org.scalajs.dom.BlobPart
import org.scalajs.dom.File
import org.scalajs.dom.FormData
import org.scalajs.dom.Headers
import org.scalajs.dom.HttpMethod
import org.scalajs.dom.RequestInit
import org.scalajs.dom.console.log

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps
import scala.scalajs.js
import scala.scalajs.js.Thenable.Implicits.*

import upickle.default.{read, write}

import Serializer.given

object Fetcher:
  val jsonHeaders = new Headers {
    js.Array(
      js.Array("Content-Type", "application/json; charset=UTF-8"),
      js.Array("Accept", "application/json")
    )
  }
  val jsonParameters = new RequestInit {
    method = HttpMethod.POST
    headers = jsonHeaders
  }
  val formDataHeaders = new Headers {
    js.Array()
  }
  val formDataParameters = new RequestInit {
    method = HttpMethod.POST
    headers = formDataHeaders
  }

  def now: Future[String] =
    ( for
        response <- dom.fetch(Urls.now)
        text <- response.text()
      yield text
    ).recover { case error: Exception => s"Now failed: ${error.getMessage}" }

  // Uncomment to enable client formdata support.
  def call(command: Command, handler: Either[Fault, Event] => Unit) =
    val event: Future[Event] = command match
      case Register(_, _, _, _) => post(command, jsonParameters, Urls.register)
      case Login(_, _) => post(command, jsonParameters, Urls.login)
      case SaveUser(_) => post(command, jsonParameters, Urls.userSave)
      case AddWorkOrder(_, _) =>
        // addWorkOrder: AddWorkOrder =>
        // val formData = addWorkOrderToFormData(addWorkOrder)
        // post(formData, formDataParameters, Urls.workOrderAdd)
        post(command, jsonParameters, Urls.workOrderAdd)
      case SaveWorkOrder(_, _) =>
        // saveWorkOrder: SaveWorkOrder =>
        // val formData = saveWorkOrderToFormData(command)
        // post(formData, formDataParameters, Urls.workOrderAdd)
        post(command, jsonParameters, Urls.workOrderSave)
      case ListWorkOrders(_, _) => post(command, jsonParameters, Urls.workOrdersList)

    handle(event, handler)

  // Uncomment to enable client formdata support.
  private def post(command: Command, parameters: RequestInit, url: String): Future[Event] =
    parameters.body = write[Command](command)
    // command: FormData | Command
    // parameters.body = if command.isInstanceOf[FormData] then command.asInstanceOf[FormData] else write[Command](command.asInstanceOf[Command]) 
    log("fetcher:post command: %s url: %s parameters: %s", command, url, parameters)
    (
      for
        response <- dom.fetch(url, parameters)
        text <- response.text()
        event = read[Event](text)
      yield
        log("fetcher:post text: %s event: %o", text, event)
        event
      ).recover { case error: Exception =>
        log("fetcher:post failure: %s", error.getCause)
        Fault(error)
      }

  private def handle(future: Future[Event], handler: Either[Fault, Event] => Unit): Unit =
    future map { event =>
      handler(
        event match
          case fault: Fault =>
            log("fetcher:handle fault: %o", fault)
            Left(fault)
          case event: Event =>
            log("fetcher:handle event: %o", event)
            if event.success then Right(event) else Left(Fault(event.error))
      )
    }

  private def addWorkOrderToFormData(addWorkOrder: AddWorkOrder): FormData =
    val workOrder = addWorkOrder.workOrder.copy(imageUrl = Model.imageFileUrl)
    workOrderToFormData(addWorkOrder.copy(workOrder = workOrder))

  private def saveWorkOrderToFormData(saveWorkOrder: SaveWorkOrder): FormData =
    val workOrder = saveWorkOrder.workOrder.copy(imageUrl = Model.imageFileUrl)
    workOrderToFormData(saveWorkOrder.copy(workOrder = workOrder))

  private def workOrderToFormData(command: AddWorkOrder | SaveWorkOrder): FormData =
    val formData = new FormData()
    val imageFile = Model.imageFile
    log("fetcher: model image file: %o", imageFile)
    if imageFile.isDefined then
      val image = imageFile.get
      formData.append("imageFileName", image.filename)
      formData.append("image", image.file, image.filename)
      log("fetcher: real image file: %s", image.filename)
    else
      val filename = s"z-${DateTime.now}.txt"
      val file = new File(new js.Array(0), "delete me!")
      formData.append("imageFileName", filename)
      formData.append("image", file, filename)
      log("fetcher: fake image file: %s", filename)
    command match
      case addWorkOrder: AddWorkOrder =>  formData.append("addWorkOrderAsJson", write[AddWorkOrder](addWorkOrder))
      case saveWorkOrder: SaveWorkOrder => formData.append("saveWorkOrderAsJson", write[SaveWorkOrder](saveWorkOrder))
    log("fetcher:workOrderToFormData formdata: %o", formData)
    formData