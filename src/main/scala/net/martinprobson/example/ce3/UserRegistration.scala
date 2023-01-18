package net.martinprobson.example.ce3

import cats.effect.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import UserNotifier.*
import UserModel.*

// Service
trait UserRegistration:
  // TODO Convert to IO[Either[String,(User,String)]]
  def register(user: User): IO[User]

object UserRegistration:

  // Implementation
  case class UserRegistrationImpl(
      userModel: IO[UserModel],
      userNotifier: IO[UserNotifier]
  ) extends UserRegistration:
    def log: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

    override def register(user: User): IO[User] =
      userModel.flatMap { um =>
        um.insert(user)
          .redeemWith(
            ex => log.error(s"Failed to insert $user due to $ex"),
            usr => {
              log.info(s"Inserted $usr") >>
                userNotifier.flatMap { un => un.notify(usr, "Welcome!") } >>
                log.info(s"Notified $usr")
            }
          )
      } >>
        IO(user)

//    override def register(user: User): IO[User] = for
//      um <- userModel
//      un <- userNotifier
//      result <- um.insert(user).attempt
//      u <- result match {
//        case Left(ex) => log.error(s"Failed to insert $user - $ex") >> IO(user)
//        case Right(u) =>
//          log.info(s"Inserted $u")
//          un.notify(u, "Welcome!")
//          log.info(s"Notified $u")
//          IO(u)
//      }
//    yield u

  def apply(
      userModel: IO[UserModel],
      userNotifier: IO[UserNotifier]
  ): IO[UserRegistration] =
    IO(UserRegistrationImpl(userModel, userNotifier))

end UserRegistration
