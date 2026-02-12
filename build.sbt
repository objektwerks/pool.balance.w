import org.scalajs.linker.interface.ModuleSplitStyle

name := "pool.balance.w"

val laminarVersion = "17.2.1"
val waypointVersion = "8.0.1" // Don't upgrade due to breaking changes!
val scalaJavaTimeVersion = "2.6.0"
val jsoniterVersion = "2.38.8"
val scalaTestVersion = "3.2.19"
val oxVersion = "1.0.3"

lazy val common = Defaults.coreDefaultSettings ++ Seq(
  organization := "objektwerks",
  version := "2.0.0",
  scalaVersion := "3.8.2-RC1",
  parallelExecution := false,
  scalacOptions ++= Seq(
    "-Wunused:all"
  )
)

lazy val poolbalance: Project = (project in file("."))
  .aggregate(sharedJs, sharedJvm, client, server)
  .settings(common)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(common)
  .settings(
    libraryDependencies ++= Seq(
      "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % jsoniterVersion,
      "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % jsoniterVersion % Provided,
      "io.github.cquiroz" %% "scala-java-time" % scalaJavaTimeVersion,
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test
    )
  )

lazy val sharedJs = shared.js
lazy val sharedJvm = shared.jvm
lazy val public = "public"

lazy val client = (project in file("client"))
  .enablePlugins(ScalaJSPlugin, ScalablyTypedConverterExternalNpmPlugin)
  .dependsOn(sharedJs)
  .settings(common)
  .settings(
    libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % laminarVersion,
      "com.raquo" %%% "waypoint" % waypointVersion,
      "com.github.plokhotnyuk.jsoniter-scala" %%% "jsoniter-scala-core" % jsoniterVersion,
      "com.github.plokhotnyuk.jsoniter-scala" %%% "jsoniter-scala-macros" % jsoniterVersion % "compile-internal",
      "io.github.cquiroz" %%% "scala-java-time" % scalaJavaTimeVersion
    ),
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("pool")))
    },
    externalNpm := {
      poolbalance.base.getAbsoluteFile()
    },
    Compile / fastLinkJS / scalaJSLinkerOutputDirectory := target.value / public,
    Compile / fullLinkJS / scalaJSLinkerOutputDirectory := target.value / public
  )

lazy val server = (project in file("server"))
  .enablePlugins(JavaServerAppPackaging)
  .dependsOn(sharedJvm)
  .settings(common)
  .settings(
    reStart / mainClass := Some("pool.Server"),
    libraryDependencies ++= {
      val helidonVersion = "4.3.4"
      Seq(
        "io.helidon.webserver" % "helidon-webserver" % helidonVersion,
        "io.helidon.webserver" % "helidon-webserver-cors" % helidonVersion,
        "com.softwaremill.ox" %% "core" % oxVersion,
        "org.scalikejdbc" %% "scalikejdbc" % "4.3.5",
        "com.zaxxer" % "HikariCP" % "7.0.2" exclude("org.slf4j", "slf4j-api"),
        "org.postgresql" % "postgresql" % "42.7.8",
        "com.github.blemale" %% "scaffeine" % "5.3.0",
        "org.jodd" % "jodd-mail" % "7.1.0",
        "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % jsoniterVersion,
        "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
        "com.typesafe" % "config" % "1.4.3",
        "ch.qos.logback" % "logback-classic" % "1.5.25",
        "org.scalatest" %% "scalatest" % scalaTestVersion % Test
      )
    }
  )
