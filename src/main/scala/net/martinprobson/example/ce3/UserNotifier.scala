package net.martinprobson.example.ce3

import cats.effect.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

// Service
trait UserNotifier:
  def notify(user: User, msg: String): IO[Unit]

object UserNotifier:

  // Implementation
  case object UserNotifierImpl extends UserNotifier:
    def log: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

    override def notify(user: User, msg: String): IO[Unit] =
      IO(s"Sending $msg to ${user.email}") >> log.info(s"Sent $msg to $user")

  def apply: IO[UserNotifier] = IO(UserNotifierImpl)

end UserNotifier
