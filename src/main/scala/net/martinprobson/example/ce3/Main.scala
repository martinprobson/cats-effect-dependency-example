package net.martinprobson.example.ce3

import cats.effect.*
import cats.effect.syntax.all.*
import cats.implicits.*
import doobie.Transactor
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import User.*

object Main extends IOApp.Simple:

  def log: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = DBTransactor.transactor.use { xa =>
    log.info("Program starting") *> program(xa) *> log.info("Program exit")
  }

  def program(xa: Transactor[IO]) = for
    // Setup dependencies...
    db <- RelationalDB.apply(xa)
    userRegistration <- UserRegistration.apply(
      UserModel.apply(IO(db)),
      UserNotifier.apply
    )
    _ <- log.debug("Create user table..")
    result <- db.createTable
    _ <- log.debug(s"Create table result = $result")
    // Generate some users to try out the code...
    _ <- Range(1, 5000).toList
      .map { i => User(UserName(s"User-$i"), Email(s"email-$i")) }
      // parTraverseN to limit the number of threads created on the blocking thread pool
      .parTraverseN(100)(u => userRegistration.register(u))
      .void
  yield ()

end Main
