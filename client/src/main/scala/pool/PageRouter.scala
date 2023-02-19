package pool

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L.*
import com.raquo.waypoint.*

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

import Page.given
import Serializer.given

object PageRouter:
  val poolRoute = Route[PoolPage, Long](
    encode = page => page.id,
    decode = arg => PoolPage(id = arg),
    pattern = root / "app" / "pools" / segment[Long] / endOfSegments
  )

  val cleaningRoute = Route[CleaningPage, Long](
    encode = page => page.id,
    decode = arg => CleaningPage(id = arg),
    pattern = root / "app" / "pool" / "cleaning" / segment[Long] / endOfSegments
  )

  val measurementRoute = Route[MeasurementPage, Long](
    encode = page => page.id,
    decode = arg => MeasurementPage(id = arg),
    pattern = root / "app" / "pool" / "measurement" / segment[Long] / endOfSegments
  )

  val chemicalRoute = Route[ChemicalPage, Long](
    encode = page => page.id,
    decode = arg => ChemicalPage(id = arg),
    pattern = root / "app" / "pool" / "chemical" / segment[Long] / endOfSegments
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
    .collectStatic(PoolsPage) { PoolsView(Model.poolsModel, Model.accountVar.now().license) }
    .collect[PoolPage] { page => PoolView(Model.poolsModel.setSelectedEntityById(page.id), Model.accountVar.now().license) }
    .collectStatic(CleaningsPage) { CleaningsView(Model.poolsModel.selectedEntityVar.now().id, Model.cleaningsModel, Model.accountVar.now().license) }
    .collect[CleaningPage] { page => CleaningView(Model.cleaningsModel.setSelectedEntityById(page.id), Model.accountVar.now().license) }
    .collectStatic(MeasurementsPage) { MeasurementsView(Model.poolsModel.selectedEntityVar.now().id, Model.measurementsModel, Model.accountVar.now().license) }
    .collect[MeasurementPage] { page => MeasurementView(Model.measurementsModel.setSelectedEntityById(page.id), Model.accountVar.now().license) }
    .collectStatic(ChemicalsPage) { ChemicalsVew(Model.poolsModel.selectedEntityVar.now().id, Model.chemicalsModel, Model.accountVar.now().license) }
    .collect[ChemicalPage] { page => ChemicalView(Model.chemicalsModel.setSelectedEntityById(page.id), Model.accountVar.now().license) }