package net.martinprobson.example.ce3

import cats.effect.{IO, Ref}
import cats.implicits.toTraverseOps
import org.typelevel.log4cats.slf4j.Slf4jLogger
import User.*

import scala.collection.immutable.SortedMap

case class InMemoryDB(
    db: Ref[IO, SortedMap[USER_ID, User]],
    counter: Ref[IO, USER_ID]
) extends UserRepository:

  override def insertUser(user: User): IO[User] = for {
    log <- Slf4jLogger.create[IO]
    id <- counter.modify(x => (x + 1, x + 1))
    _ <- log.debug(s"About to create : $user")
    _ <- db.update(users => users.updated(key = id, value = user))
    user <- IO(User(id, user.name, user.email))
    _ <- log.debug(s"Created user: $user")
  } yield user

  override def createTable: IO[Int] = ???

  override def countUsers: IO[Long] = db.get.flatMap { users =>
    IO(users.size.toLong)
  }
end InMemoryDB

object InMemoryDB:

  def empty: IO[UserRepository] = for {
    counter <- Ref[IO].of(0L)
    db <- Ref[IO].of(SortedMap.empty[USER_ID, User])
  } yield new InMemoryDB(db, counter)
