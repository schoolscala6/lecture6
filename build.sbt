name := "doobie-simple-app"

version := "0.1"

scalaVersion := "2.13.4"

lazy val PureConfigVersion = "0.14.0"
lazy val DoobieVersion = "0.9.0"
lazy val LogbackVersion = "1.2.3"
lazy val ScalaTestVersion = "3.2.2"
lazy val ScalaMockVersion = "5.1.0"

libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core" % DoobieVersion,
  "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
  "org.tpolecat" %% "doobie-h2" % DoobieVersion,
  "org.tpolecat" %% "doobie-hikari" % DoobieVersion,
  "org.tpolecat" %% "doobie-quill" % DoobieVersion,
  "org.tpolecat" %% "doobie-specs2" % DoobieVersion,
  "org.tpolecat" %% "doobie-scalatest" % DoobieVersion,

  "io.zonky.test" % "embedded-postgres" % "1.2.9",

  "com.github.pureconfig" %% "pureconfig" % PureConfigVersion,
  "com.github.pureconfig" %% "pureconfig-cats-effect" % PureConfigVersion,

  "ch.qos.logback" % "logback-classic" % LogbackVersion,

  "org.scalatest" %% "scalatest" % ScalaTestVersion,
  "org.scalamock" %% "scalamock" % ScalaMockVersion
)