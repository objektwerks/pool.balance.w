package pool

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L.*
import com.raquo.waypoint.*

import com.github.plokhotnyuk.jsoniter_scala.core.*

import Page.given

object PageRouter:
  val poolsRoute = Route.static(PoolsPage, root / "app" / "pools" / endOfSegments)
  val poolRoute = Route[PoolPage, Long](
    encode = page => page.id,
    decode = arg => PoolPage(id = arg),
    pattern = root / "app" / "pools" / segment[Long] / endOfSegments
  )

  val cleaningsRoute = Route.static(CleaningsPage, root / "app" / "pool" / "cleanings" / endOfSegments)
  val cleaningsChartRoute = Route.static(CleaningsChartPage, root / "app" / "pool" / "cleanings" / "chart" / endOfSegments)
  val cleaningRoute = Route[CleaningPage, (Long, Long)](
    encode = page => (page.poolId, page.id),
    decode = (poolId, id) => CleaningPage(poolId, id),
    pattern = root / "app" / "pools" / segment[Long] / "cleanings" / segment[Long] / endOfSegments
  )

  val measurementsRoute = Route.static(MeasurementsPage, root / "app" / "pools" / "pool" / "measurements" / endOfSegments)
  val measurementsChartRoute = Route.static(MeasurementsPage, root / "app" / "pools" / "pool" / "measurements" / "chart" / endOfSegments)
  val measurementRoute = Route[MeasurementPage, (Long, Long)](
    encode = page => (page.poolId, page.id),
    decode = (poolId, id) => MeasurementPage(poolId, id),
    pattern = root / "app" / "pools" / segment[Long] / "measurements" / segment[Long] / endOfSegments
  )

  val chemicalsRoute = Route.static(ChemicalsPage, root / "app" / "pools" / "pool" / "chemicals" / endOfSegments)
  val chemicalsChartRoute = Route.static(ChemicalsPage, root / "app" / "pools" / "pool" / "chemicals" / "chart" / endOfSegments)
  val chemicalRoute = Route[ChemicalPage, (Long, Long)](
    encode = page => (page.poolId, page.id),
    decode = (poolId, id) => ChemicalPage(poolId, id),
    pattern = root / "app" / "pools" / segment[Long] / "chemicals" / segment[Long] / endOfSegments
  )

  val routees = List(
    Route.static(HomePage, root / endOfSegments),

    Route.static(RegisterPage, root / "register" / endOfSegments),
    Route.static(LoginPage, root / "login" / endOfSegments),

    Route.static(AppPage, root / "app" / endOfSegments),
    Route.static(AccountPage, root / "app" / "account" / endOfSegments),

    poolsRoute,
    poolRoute,

    cleaningsRoute,
    cleaningsChartRoute,
    cleaningRoute,

    measurementsRoute,
    measurementsChartRoute,
    measurementRoute,

    chemicalsRoute,
    chemicalsChartRoute,
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

    .collectStatic(PoolsPage) { PoolsView(Model.pools, Model.license) }
    .collect[PoolPage] { page => PoolView(Model.pools.setSelectedEntityById(page.id), Model.license) }

    .collectStatic(CleaningsPage) { CleaningsView(Model.pools.selectedEntityVar.now().id, Model.cleanings, Model.license) }
    .collectStatic(CleaningsChartPage) { CleaningsChartView(Model.cleanings) }
    .collect[CleaningPage] { page => CleaningView(Model.cleanings.setSelectedEntityById(page.id), Model.license) }

    .collectStatic(MeasurementsPage) { MeasurementsView(Model.pools.selectedEntityVar.now().id, Model.measurements, Model.license) }
    .collect[MeasurementPage] { page => MeasurementView(Model.measurements.setSelectedEntityById(page.id), Model.license) }

    .collectStatic(ChemicalsPage) { ChemicalsView(Model.pools.selectedEntityVar.now().id, Model.chemicals, Model.license) }
    .collect[ChemicalPage] { page => ChemicalView(Model.chemicals.setSelectedEntityById(page.id), Model.license) }