
name := "sbt-getting-started"

version := "0.1"

scalaVersion := "2.12.0"



lazy val calculator = project

lazy val api = project
  .settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "requests" % "0.1.7",
      "org.scala-lang.modules" %% "scala-xml" % "1.1.1"
    )
  )