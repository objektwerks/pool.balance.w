package workorder

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L.*
import com.raquo.waypoint.*

import upickle.default.*

import Serializer.given

object PageRouter:
  given pageRW: ReadWriter[Page] = macroRW

  val routes = List(
    Route.static(LoginRegisterPage, root / endOfSegments),
    Route.static(RegisterPage, root / endOfSegments),
    Route.static(LoginPage, root / endOfSegments),
    Route.static(ProfilePage, root / endOfSegments),
    Route.static(WorkOrdersPage, root / endOfSegments),
    Route.static(WorkOrderPage, root / endOfSegments)
  )

  val router = com.raquo.waypoint.Router[Page](
    routes = routes,
    serializePage = page => write(page)(pageRW),
    deserializePage = pageAsString => read(pageAsString)(pageRW),
    getPageTitle = _.title,
  )(
    $popStateEvent = L.windowEvents.onPopState,
    owner = L.unsafeWindowOwner
  )

  val splitter = SplitRender[Page, HtmlElement](router.$currentPage)
    .collectStatic(LoginRegisterPage) { LoginRegisterView() }
    .collectStatic(LoginPage) { LoginView() }
    .collectStatic(RegisterPage) { RegisterView() }
    .collectStatic(ProfilePage) { ProfileView() }
    .collectStatic(WorkOrdersPage) { WorkOrdersView() }
    .collectStatic(WorkOrderPage) { WorkOrderView() }