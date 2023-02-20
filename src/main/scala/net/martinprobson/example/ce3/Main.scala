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

  private def program(xa: Transactor[IO]) = for
    // Setup dependencies...
    // db <- RelationalDB(xa)
    db <- InMemoryDB.empty
    userModel <- UserModel(db)
    emailService <- EmailService.apply
    userNotifier <- UserNotifier(emailService)
    userRegistration <- UserRegistration(userModel, userNotifier)
    _ <- log.debug("Create user table..")
    result <- db.createTable
    _ <- log.debug(s"Create table result = $result")
    // Generate some users to try out the code...
    _ <- Range(1, 30).inclusive.toList
      .map { i => User(UserName(s"User-$i"), Email(s"email-$i")) }
      // parTraverseN to limit the number of threads created on the blocking thread pool
      .parTraverseN(100)(u =>
        userRegistration.register(u).flatMap {
          case Left(err) => log.error(s"Error: $err")
          case Right(u)  => log.info(s"Registered User: $u")
        }
      )
      .void
    count <- db.countUsers
    _ <- log.info(s"Total of $count users registered.")
  yield ()

end Main
