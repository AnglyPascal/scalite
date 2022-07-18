import Dependencies._

ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.anglypascal"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "scalite",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "org.planet42" %% "laika-io" % "0.18.2",
    libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % "3.6.2",
    /* libraryDependencies += "io.circe" %% "circe-yaml" % "0.14.1", */
    /* libraryDependencies += "io.circe" %% "circe-parser" % "0.14.1", */
   libraryDependencies ++= Seq(
     "com.rallyhealth" %% "weepickle-v1" % "1.7.2",
     "com.rallyhealth" %% "weeyaml-v1" % "1.7.2"
   ),
   libraryDependencies += "com.anglypascal" %% "mustache" % "0.1.2-SNAPSHOT",
  )

scalacOptions += "-deprecation"
// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
