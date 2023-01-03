package net.martinprobson.example.ce3

import cats.effect.*
import com.zaxxer.hikari.HikariDataSource
import doobie.Transactor
import doobie.*
import doobie.free.connection.ConnectionOp
import doobie.implicits.*
import cats.free.Free
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import User.*

// service
trait DB:
  def insertUser(user: User): IO[User]

object DB:

  def log: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  // Implementation
  case class RelationalDB(xa: Transactor[IO]) extends DB:

    createTable
    def createTable: IO[Int] =
      sql"""
           |create table if not exists user
           |(
           |    id   int auto_increment
           |        primary key,
           |    name varchar(100) null
           |);
           |""".stripMargin.update.run.transact(xa)

    override def insertUser(user: User): IO[User] = (for {
      _ <-
        sql"INSERT INTO User (name, email) VALUES (${user.name.toString},${user.email.toString})".update.run
      id <- sql"SELECT last_insert_id()".query[Long].unique
      user <- Free.pure[ConnectionOp, User](
        User(id, user.name, user.email)
      )
    } yield user).transact(xa)

  object RelationalDB:
    def createTable(xa: Transactor[IO]): IO[Int] =
      sql"""
           |create table if not exists user
           |(
           |    id   int auto_increment
           |        primary key,
           |    name varchar(100) null,
           |    email varchar(100) null
           |);
           |""".stripMargin.update.run.transact(xa)

  def apply(xa: Transactor[IO]): IO[DB] = for {
    _ <- RelationalDB.createTable(xa)
    db <- IO(RelationalDB(xa))
  } yield db

end DB
