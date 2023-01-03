ThisBuild / organization := "net.martinprobson.example"
ThisBuild / scalaVersion := "3.2.1"

lazy val root = (project in file(".")).settings(
  name := "cats-effect-dependency-example",
  libraryDependencies ++= Seq(
    // "core" module - IO, IOApp, schedulers
    // This pulls in the kernel and std modules automatically.
    "org.typelevel" %% "cats-effect" % "3.3.12",
    // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)
    "org.typelevel" %% "cats-effect-kernel" % "3.3.12",
    // standard "effect" library (Queues, Console, Random etc.)
    "org.typelevel" %% "cats-effect-std" % "3.3.12",
    // logging
    "ch.qos.logback" % "logback-classic" % "1.2.11",
    "ch.qos.logback" % "logback-core" % "1.2.11",
    "org.typelevel" %% "log4cats-slf4j" % "2.5.0",
    // Config
    "com.github.japgolly.clearconfig" %% "core" % "3.0.0",
    // db drivers
    "org.tpolecat" %% "doobie-core" % "1.0.0-RC1",
    "org.tpolecat" %% "doobie-hikari" %  "1.0.0-RC1",
    "mysql" % "mysql-connector-java" % "8.0.30",
    "com.h2database" % "h2" % "1.4.200",
    // testing
    "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test
  )
)

lazy val compilerOptions = Seq(
  "-deprecation",         // Emit warning and location for usages of deprecated APIs.
  "-explaintypes",        // Explain type errors in more detail.
  "-explain",
  "-Xfatal-warnings",     // Fail the compilation if there are any warnings.
  "-encoding",
  "utf8"
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions
)
