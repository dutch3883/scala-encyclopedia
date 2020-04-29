// The simplest possible sbt build file is just one line:

ThisBuild / scalaVersion := "2.12.10"
// That is, to create a valid sbt build, all you've got to do is define the
// version of Scala you'd like your project to use.

// ============================================================================

// Lines like the above defining `scalaVersion` are called "settings". Settings
// are key/value pairs. In the case of `scalaVersion`, the key is "scalaVersion"
// and the value is "2.13.1"

// It's possible to define many kinds of settings, such as:

name := "hello-world"
organization := "ch.epfl.scala"
version := "1.0"

lazy val sbtStart  = project.in(file("sbt-getting-started"))


val circeVersion = "0.12.3"
val catVersion = "2.0.0"

libraryDependencies += "org.typelevel" %% "cats-core" % catVersion
libraryDependencies += "org.typelevel" %% "cats-effect" % catVersion
libraryDependencies += "org.typelevel" %% "cats-effect" % catVersion
libraryDependencies += "io.circe" %% "circe-core" % circeVersion
libraryDependencies += "io.circe" %% "circe-generic" % circeVersion
libraryDependencies += "io.circe" %% "circe-parser" % circeVersion
libraryDependencies += "org.sangria-graphql" %% "sangria-spray-json" % "1.0.1"
libraryDependencies += "org.sangria-graphql" %% "sangria" % "1.4.1"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.0"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.0"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.11"