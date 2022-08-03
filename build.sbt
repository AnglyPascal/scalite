val scala3Version = "3.1.3"

lazy val root = project
  .in(file("."))
  .settings(
    name         := "scalite",
    version      := "0.1.1",
    organization := "com.anglypascal",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit"     % "0.7.29" % Test,
      "org.scalatest" %% "scalatest" % "3.2.12"  % Test,
      "com.rallyhealth" %% "weepickle-v1" % "1.7.2",
      "com.rallyhealth" %% "weeyaml-v1" % "1.7.2",
      "com.softwaremill.sttp.client3" %% "core" % "3.6.2",
      "org.planet42" %% "laika-io" % "0.18.2",
      "com.github.nscala-time" %% "nscala-time" % "2.30.0",
    ),
   libraryDependencies += "com.anglypascal" %% "scala3-mustache" % "0.1.1",
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-feature"
  /* "-explain" */
)
