name := "concurrent-poc"

organization := "fr.mleduc"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

scalaVersion := "2.11.8"

crossScalaVersions := Seq("2.10.4", "2.11.4")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "com.typesafe.akka" %% "akka-actor" % "2.4.11",
  "org.scalaz" %% "scalaz-core" % "7.2.6",
  "org.scala-graph" %% "graph-core" % "1.11.3"
)



bintraySettings

com.typesafe.sbt.SbtGit.versionWithGit
