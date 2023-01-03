package net.martinprobson.example.ce3

import cats.effect.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import UserNotifier.*
import UserModel.*

// Service
trait UserRegistration:
  def register(user: User): IO[User]

object UserRegistration:

  // Implementation
  case class UserRegistrationImpl(
      userModel: IO[UserModel],
      userNotifier: IO[UserNotifier]
  ) extends UserRegistration:
    def log: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

    override def register(user: User): IO[User] = for
      u <- userModel.flatMap(_.insert(user))
      _ <- log.info(s"Insert: $u")
      _ <- userNotifier.flatMap(_.notify(u, "Welcome!"))
    yield u

  def apply(
      userModel: IO[UserModel],
      userNotifier: IO[UserNotifier]
  ): IO[UserRegistration] =
    IO(UserRegistrationImpl(userModel, userNotifier))

end UserRegistration
