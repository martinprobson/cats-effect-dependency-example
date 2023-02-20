package net.martinprobson.example.ce3

import cats.effect.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import DatabaseInsertError.*

// service
trait UserModel:
  // TODO Convert to IO[Either[String,User]]???
  def insert(user: User): IO[Either[RegistrationError, User]]

object UserModel:
  // Implementation
  case class UserModelImpl(db: UserRepository) extends UserModel:
    def log: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

    // TODO Convert to IO[Either[String,User]]???
    override def insert(user: User): IO[Either[RegistrationError, User]] =
      for {
        u <- db
          .insertUser(user)
          .redeem(
            ex => Left(DatabaseInsertError(user, "Failed to insert")),
            u => Right(u)
          )
        _ <- log.debug(s"Inserted user: $u")
      } yield u

  def apply(db: UserRepository): IO[UserModel] = IO(UserModelImpl(db))

end UserModel
