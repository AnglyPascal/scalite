val scala3Version = "3.1.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scalite",
    version := "0.1.1",
    organization := "com.anglypascal",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "org.scalatest" %% "scalatest" % "3.2.12" % Test
    ),
    libraryDependencies ++= Seq(
      "com.rallyhealth" %% "weepickle-v1" % "1.7.2",
      "com.rallyhealth" %% "weeyaml-v1" % "1.7.2",
      "com.softwaremill.sttp.client3" %% "core" % "3.6.2",
      "org.planet42" %% "laika-core" % "0.18.2", 
      "com.github.nscala-time" %% "nscala-time" % "2.30.0",
      "io.lemonlabs" %% "scala-uri" % "4.0.2",
      "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4"
    ),
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.2.10",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4"
    ),
    libraryDependencies += "com.anglypascal" %% "scala3-mustache" % "0.1.2.1",
    libraryDependencies += "de.larsgrefer.sass" % "sass-embedded-host" % "1.6.1"
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-feature"
  /* "-explain" */
)
