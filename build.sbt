lazy val common = Defaults.coreDefaultSettings ++ Seq(
  organization := "objektwerks",
  version := "0.9-SNAPSHOT",
  scalaVersion := "3.2.2",
  libraryDependencies ++= {
    val jsoniterVersion = "2.20.6"
    Seq(
      "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % jsoniterVersion,
      "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % jsoniterVersion % Provided,
      "com.typesafe" % "config" % "1.4.2"
    )
  }
)

lazy val poolbalance = (project in file("."))
  .aggregate(client, shared, server)
  .settings(common)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val client = project
  .dependsOn(shared)
  .settings(common)
  .settings(
    libraryDependencies ++= {
      Seq(
        "org.scalafx" %% "scalafx" % "19.0.0-R30",
        "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
        "ch.qos.logback" % "logback-classic" % "1.4.5"
      )
    }
  )
  .settings(
    libraryDependencies ++= Seq("base", "controls", "web").map( jfxModule =>
      "org.openjfx" % s"javafx-$jfxModule" % "19" classifier OS
    )
  )

lazy val shared = project
  .settings(common)
  .settings(
    libraryDependencies ++= {
      Seq(
        "org.scalafx" %% "scalafx" % "19.0.0-R30"
         exclude("org.openjfx", "javafx-controls")
         exclude("org.openjfx", "javafx-fxml")
         exclude("org.openjfx", "javafx-graphics")
         exclude("org.openjfx", "javafx-media")
         exclude("org.openjfx", "javafx-swing")
         exclude("org.openjfx", "javafx-web"),
        "org.scalatest" %% "scalatest" % "3.2.15" % Test
      )
    }
  )

lazy val server = project
  .enablePlugins(JavaServerAppPackaging)
  .dependsOn(shared)
  .settings(common)
  .settings(
    libraryDependencies ++= {
      Seq(
        "org.scalikejdbc" %% "scalikejdbc" % "4.0.0",
        "com.zaxxer" % "HikariCP" % "5.0.1" exclude("org.slf4j", "slf4j-api"),
        "org.postgresql" % "postgresql" % "42.5.3",
        "com.github.blemale" %% "scaffeine" % "5.2.1",
        "org.jodd" % "jodd-mail" % "6.0.5",
        "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
        "ch.qos.logback" % "logback-classic" % "1.4.5",
        "org.scalatest" %% "scalatest" % "3.2.15" % Test
      )
    }
  )
