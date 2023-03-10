package net.martinprobson.example.ce3

import cats.effect.IO
import cats.implicits.*
import japgolly.clearconfig.*

final case class Config(
                         threads: Int,
                         driverClassName: String,
                         url: String,
                         user: String,
                         password: String
                       )

object Config:
  private def configSources: ConfigSources[IO] =
    ConfigSource.environment[IO] >
      ConfigSource.propFileOnClasspath[IO]("/application.properties", optional = false) >
      ConfigSource.system[IO]

  private def cfg: ConfigDef[Config] = (
    ConfigDef.need[Int]("threads"),
    ConfigDef.need[String]("driverClassName"),
    ConfigDef.need[String]("url"),
    ConfigDef.need[String]("user"),
    ConfigDef.need[String]("password"),
  ).mapN(apply)

  val loadConfig: IO[Config] = Config.cfg.run(configSources).flatMap(r => IO(r.getOrDie()))

end Config

