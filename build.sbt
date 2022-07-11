import Dependencies._

ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "scalite",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "org.planet42" %% "laika-io" % "0.18.2",
    libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % "3.6.2",
  )

scalacOptions += "-deprecation"
// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
