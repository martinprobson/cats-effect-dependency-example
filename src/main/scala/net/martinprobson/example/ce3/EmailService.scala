package net.martinprobson.example.ce3

import cats.effect.IO
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import User.*

// service
trait EmailService:

  def send(email: Email, msg: String): IO[Unit]

end EmailService

object EmailService:

  // Implementation
  private case object EmailServiceImpl extends EmailService:

    def log: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

    override def send(email: Email, msg: String): IO[Unit] =
      log.debug(s"Sending $msg to $email")

  def apply: IO[EmailService] = IO(EmailServiceImpl)

end EmailService
