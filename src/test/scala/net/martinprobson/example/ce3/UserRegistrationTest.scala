package net.martinprobson.example.ce3

import cats.effect.*
import cats.effect.syntax.all.*
import cats.implicits.*
import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers.*
import User.*

class UserRegistrationTest extends AsyncFunSuite with AsyncIOSpec:

  object MockUserNotifier extends UserNotifier:
    override def notify(user: User, msg: String): IO[String] = IO(
      s"Notified $user with $msg"
    )
  end MockUserNotifier

  test("Single user") {
    val user = User(1, UserName("testuser"), Email("testemail"))
    (for {
      db <- InMemoryDB.empty
      userRegistration <- UserRegistration(
        UserModel.apply(IO(db)),
        IO(MockUserNotifier)
      )
      result <- userRegistration.register(user)
      count <- db.countUsers
    } yield (result, count)).asserting {
      _ shouldBe ((user, s"Notified $user with Welcome!"), 1L)
    }
  }

  test("Multiple users") {
    (for {
      db <- InMemoryDB.empty
      userRegistration <- UserRegistration(
        UserModel.apply(IO(db)),
        IO(MockUserNotifier)
      )
      _ <- Range
        .inclusive(1, 10000)
        .toList
        .map { i => User(UserName(s"User-$i"), Email(s"email-$i")) }
        // parTraverseN to limit the number of threads created on the blocking thread pool
        .parTraverseN(100)(u => userRegistration.register(u))
        .void
      count <- db.countUsers
    } yield count).asserting { _ shouldBe 10000L }
  }

end UserRegistrationTest
