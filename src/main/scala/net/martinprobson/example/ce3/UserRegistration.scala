package net.martinprobson.example.ce3

import cats.effect.*
import cats.*
import cats.implicits.*
import cats.data.*
import cats.syntax.all.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import UserNotifier.*
import UserModel.*

// Service
trait UserRegistration:
  // TODO Convert to IO[Either[String,(User,String)]]
  def register(user: User): IO[Either[RegistrationError, User]]

object UserRegistration:

  // Implementation
  case class UserRegistrationImpl(
      userModel: UserModel,
      userNotifier: UserNotifier
  ) extends UserRegistration:
    def log: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

    override def register(user: User): IO[Either[RegistrationError, User]] =
      (for {
        usr <- EitherT(userModel.insert(user))
        msg <- EitherT(userNotifier.notify(user, "Welcome!"))
      } yield usr).value

  def apply(
      userModel: UserModel,
      userNotifier: UserNotifier
  ): IO[UserRegistration] =
    IO(UserRegistrationImpl(userModel, userNotifier))

end UserRegistration
