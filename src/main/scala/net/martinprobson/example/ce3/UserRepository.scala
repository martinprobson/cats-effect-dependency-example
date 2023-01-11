package net.martinprobson.example.ce3

import cats.effect.*
import com.zaxxer.hikari.HikariDataSource
import doobie.*
import doobie.free.connection.ConnectionOp
import doobie.implicits.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import User.*

// service
trait UserRepository:
  def insertUser(user: User): IO[User]

  def countUsers: IO[Long]

  def createTable: IO[Int]
end UserRepository
