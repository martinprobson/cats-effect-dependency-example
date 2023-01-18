package net.martinprobson.example.ce3

import cats.effect.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

// Service
trait UserNotifier:
  def notify(user: User, msg: String): IO[Unit]

object UserNotifier:

  // Implementation
  case class UserNotifierImpl(emailService: IO[EmailService])
      extends UserNotifier:

    def log: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

    override def notify(user: User, msg: String): IO[Unit] = for {
      _ <- log.info(s"Sending $msg to ${user.email}")
      es <- emailService
      _ <- es.send(user.email, msg)
    } yield ()

  def apply(emailService: IO[EmailService]): IO[UserNotifier] = IO(
    new UserNotifierImpl(emailService)
  )

end UserNotifier
