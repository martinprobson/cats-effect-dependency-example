package net.martinprobson.example.ce3

import cats.effect.*
import cats.effect.syntax.all.*
import cats.implicits.*
import doobie.Transactor
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import DB.*
import User.*
import UserModel.*
import UserRegistration.*

object Main extends IOApp.Simple:

  def log: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = DBTransactor.transactor.use { xa =>
    log.info("Program starting") *> program(xa) *> log.info("Program exit")
  }

  def program(xa: Transactor[IO]): IO[Unit] = for
    userRegistration <- UserRegistration.apply(
      UserModel.apply(DB.apply(xa)),
      UserNotifier.apply
    )
    _ <- Range(1, 20).toList
      .map { i => User(UserName(s"User-$i"), Email(s"email-$i")) }
      // parTraverseN to limit the number of threads created on the blocking thread pool
      .parTraverseN(100)(u => userRegistration.register(u))
  yield ()

end Main
