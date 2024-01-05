import sbtassembly.AssemblyKeys.assembly

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "odaibot"
  )

resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies ++= Seq(
  "com.github.uakihir0" % "misskey4j" % "0.5.0",
  "ch.qos.logback" % "logback-classic" % "1.4.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "org.quartz-scheduler" % "quartz" % "2.3.2",
  "com.github.pathikrit" %% "better-files" % "3.9.2",
)

assembly / assemblyJarName := "odai.jar"
assembly / assemblyMergeStrategy := {
  case PathList("javax", "servlet", xs@_*) => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".properties" => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".json" => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".factories" => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".xml" => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".types" => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".class" => MergeStrategy.first
  case "application.conf" => MergeStrategy.concat
  case "unwanted.txt" => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}