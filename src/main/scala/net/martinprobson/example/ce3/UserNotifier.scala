package net.martinprobson.example.ce3

import cats.effect.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

// Service
trait UserNotifier:
  def notify(user: User, msg: String): IO[Either[RegistrationError, String]]

object UserNotifier:

  // Implementation
  case class UserNotifierImpl(emailService: EmailService) extends UserNotifier:

    def log: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

    override def notify(
        user: User,
        msg: String
    ): IO[Either[RegistrationError, String]] = for {
      _ <- log.info(s"Sending $msg to ${user.email}")
      result <- emailService.send(user.email, msg)
    } yield Right(result)

  def apply(emailService: EmailService): IO[UserNotifier] = IO(
    new UserNotifierImpl(emailService)
  )

end UserNotifier
