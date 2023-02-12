package pool

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L.*
import com.raquo.waypoint.*

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

import Page.given
import Serializer.given

object PageRouter:
  val routes = List(
    Route.static(HomePage, root / endOfSegments)
  )

  val poolRoute = Route[PoolPage, Long](
    encode = page => page.id,
    decode = arg => PoolPage(id = arg),
    pattern = root / "app" / "pools" / segment[Long] / endOfSegments
  )

  val cleaningRoute = Route[CleaningPage, Long](
    encode = page => page.id,
    decode = arg => CleaningPage(id = arg),
    pattern = root / "app" / "pool" / segment[Long] / endOfSegments
  )

  val measurementRoute = Route[MeasurementPage, Long](
    encode = page => page.id,
    decode = arg => MeasurementPage(id = arg),
    pattern = root / "app" / "pool" / segment[Long] / endOfSegments
  )

  val chemicalRoute = Route[ChemicalPage, Long](
    encode = page => page.id,
    decode = arg => ChemicalPage(id = arg),
    pattern = root / "app" / "pool" / segment[Long] / endOfSegments
  )

  val routees = List(
    Route.static(HomePage, root / endOfSegments),
    Route.static(RegisterPage, root / "register" / endOfSegments),
    Route.static(LoginPage, root / "login" / endOfSegments),
    Route.static(AppPage, root / "app" / endOfSegments),
    Route.static(AccountPage, root / "app" / "account" / endOfSegments),
    Route.static(PoolsPage, root / "app" / "pools" / endOfSegments),
    poolRoute,
    cleaningRoute,
    measurementRoute,
    chemicalRoute
  )

  val router = com.raquo.waypoint.Router[Page](
    routes = routees,
    serializePage = page => writeToString(page),
    deserializePage = pageAsString => readFromString(pageAsString),
    getPageTitle = _.title,
  )(
    popStateEvents = L.windowEvents(_.onPopState),
    owner = L.unsafeWindowOwner
  )

  val splitter = SplitRender[Page, HtmlElement](router.currentPageSignal)
    .collectStatic(HomePage) { HomeView() }
    .collectStatic(RegisterPage) { RegisterView(Model.emailAddressVar, Model.pinVar, Model.accountVar) }
    .collectStatic(LoginPage) { LoginView(Model.emailAddressVar, Model.pinVar, Model.accountVar) }
    .collectStatic(AppPage) { AppView(Model.accountVar) }
    .collectStatic(AccountPage) { AccountView(Model.accountVar) }
    .collectStatic(PoolsPage) { PoolsView(Model.poolsModel, Model.accountVar) }
    .collect[PoolPage] { page => PoolView(Model.poolsModel.setSelectedEntityById(page.id), Model.accountVar) }