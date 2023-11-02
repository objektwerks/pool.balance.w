name := "pool.balance.w"

lazy val laminarVersion = "16.0.0"
lazy val waypointVersion = "7.0.0"
lazy val scalaJavaTimeVersion = "2.5.0"
lazy val jsoniterVersion = "2.24.3"
lazy val scalaTestVersion = "3.2.17"

lazy val common = Defaults.coreDefaultSettings ++ Seq(
  organization := "objektwerks",
  version := "0.17-SNAPSHOT",
  scalaVersion := "3.3.1",
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
      "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % jsoniterVersion % "compile-internal",
      "io.github.cquiroz" %% "scala-java-time" % scalaJavaTimeVersion,
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test
    )
  )

lazy val sharedJs = shared.js
lazy val sharedJvm = shared.jvm
lazy val public = "public"

lazy val client = (project in file("client"))
  .enablePlugins(ScalaJSPlugin) // Doesn't support Sbt 1.9.6: ScalablyTypedConverterExternalNpmPlugin
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
    useYarn := true,
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
      Seq(
        "org.scalikejdbc" %% "scalikejdbc" % "4.0.0",
        "com.zaxxer" % "HikariCP" % "5.0.1" exclude("org.slf4j", "slf4j-api"),
        "org.postgresql" % "postgresql" % "42.6.0",
        "com.github.blemale" %% "scaffeine" % "5.2.1",
        "org.jodd" % "jodd-mail" % "7.0.1",
        "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % jsoniterVersion,
        "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
        "com.typesafe" % "config" % "1.4.2",
        "ch.qos.logback" % "logback-classic" % "1.4.11",
        "org.scalatest" %% "scalatest" % scalaTestVersion % Test
      )
    }
  )
