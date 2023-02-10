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

// Begin: Client Assembly Tasks

  lazy val createAssemblyDir = taskKey[File]("Create assembly dir.")
  createAssemblyDir := {
    import java.nio.file._

    val assemblyDir: File = baseDirectory.value / ".assembly"
    val assemblyPath: Path = assemblyDir.toPath()

    if (!Files.exists(assemblyPath)) Files.createDirectory(assemblyPath)

    println(s"[createAssemblyDir] assembly dir: ${assemblyPath} is valid: ${Files.isDirectory(assemblyPath)}")

    assemblyDir
  }

  lazy val copyAssemblyJar = taskKey[Unit]("Copy assembly jar to assembly dir.")
  copyAssemblyJar := {
    import java.nio.file._

    val assemblyDir: File = createAssemblyDir.value
    val assemblyPath: String = s"${assemblyDir.toString}/${assemblyJarName.value}"

    val source: Path = (assembly / assemblyOutputPath).value.toPath
    val target: Path = Paths.get(assemblyPath)

    println(s"[copyAssemblyJar] source: ${source}")
    println(s"[copyAssemblyJar] target: ${target}")

    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)
  }

// End: Client Assembly Tasks

// Begin: Client Assembly

  /*
  See assembly section in readme.
  1. sbt -Dtarget="mac" clean test assembly copyAssemblyJar
  2. sbt -Dtarget="m1" clean test assembly copyAssemblyJar
  3. sbt -Dtarget="win" clean test assembly copyAssemblyJar
  4. sbt -Dtarget="linux" clean test assembly copyAssemblyJar
  */
  lazy val OS: String = sys.props.getOrElse("target", "") match {
    case name if name.startsWith("mac")   => "mac"
    case name if name.startsWith("m1")    => "mac-aarch64"
    case name if name.startsWith("win")   => "win"
    case name if name.startsWith("linux") => "linux"
    case _ => ""
  }

  if (OS == "mac") assemblyJarName := "pool-balance-mac-0.3.jar"
  else if (OS == "mac-aarch64") assemblyJarName := "pool-balance-m1-0.3.jar"
  else if (OS == "win") assemblyJarName := "pool-balance-win-0.3.jar"
  else if (OS == "linux") assemblyJarName := "pool-balance-linux-0.3.jar"
  else assemblyJarName := "pool-balance-no-valid-target-specified-0.3.jar"

  client / assembly / assemblyMergeStrategy := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
  }

// End: Client Assembly

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
