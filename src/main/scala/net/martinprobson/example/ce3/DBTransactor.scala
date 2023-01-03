package net.martinprobson.example.ce3

import cats.effect.{IO, Resource}
import doobie.ExecutionContexts
import doobie.hikari.HikariTransactor
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object DBTransactor:
  def log: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  /** Setup a HikariTransactor connection pool.
   *
   * @return
   * A Resource containing a HikariTransactor.
   */
  val transactor: Resource[IO, HikariTransactor[IO]] =
    (for
      _ <- Resource.eval[IO, Unit](log.info("Setting up transactor"))
      cfg <- Resource.eval[IO, Config](Config.loadConfig)
      ce <- ExecutionContexts.fixedThreadPool[IO](cfg.threads)
      xa <- HikariTransactor
        .newHikariTransactor[IO](cfg.driverClassName, cfg.url, cfg.user, cfg.password, ce)
    yield xa).onFinalize(log.info("Finalize of transactor"))

end DBTransactor
