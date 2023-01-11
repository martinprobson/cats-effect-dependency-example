package net.martinprobson.example.ce3

import cats.effect.*
import com.zaxxer.hikari.HikariDataSource
import doobie.*
import doobie.free.connection.ConnectionOp
import doobie.implicits.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

case class RelationalDB(xa: Transactor[IO]) extends UserRepository:

  private def log: SelfAwareStructuredLogger[ConnectionIO] =
    Slf4jLogger.getLogger[ConnectionIO]

  override def countUsers: IO[Long] =
    sql"SELECT COUNT(*) FROM user".query[Long].unique.transact(xa)

  override def insertUser(user: User): IO[User] = (for {
    _ <-
      sql"INSERT INTO user (name, email) VALUES (${user.name.toString},${user.email.toString})".update.run
    id <- sql"SELECT last_insert_id()".query[Long].unique
    user <- doobie.free.connection.pure(
      User(id, user.name, user.email)
    )
  } yield user).transact(xa)

  override def createTable: IO[Int] = (for {
    result <-
      sql"""
           |create table if not exists user
           |(
           |    id   int auto_increment
           |        primary key,
           |    name varchar(100) null,
           |    email varchar(100) null
           |);
           |""".stripMargin.update.run
    _ <- log.info(s"In createTable: result = $result")
  } yield result).transact(xa)

object RelationalDB:
  def apply(xa: Transactor[IO]): IO[UserRepository] = IO(new RelationalDB(xa))
