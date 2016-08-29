name := "stream-correlation-analysis"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "io.reactivex" %% "rxscala" % "0.26.1",
  "org.scalactic" %% "scalactic" % "2.2.6",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "com.typesafe.akka" %% "akka-actor" % "2.4.8",
  "dmf.stream" %% "mise" % "0.1-SNAPSHOT"
)