package net.martinprobson.example.ce3

import cats.effect.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

// service
trait UserModel:
  def insert(user: User): IO[User]

object UserModel:
  // Implementation
  case class UserModelImpl(db: IO[DB]) extends UserModel:
    def log: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

    override def insert(user: User): IO[User] = for {
      u <- db.flatMap(_.insertUser(user))
      _ <- log.info(s"Inserted user: $u")
    } yield u

  def apply(db: IO[DB]): IO[UserModel] = IO(UserModelImpl(db))

end UserModel
