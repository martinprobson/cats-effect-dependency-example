package net.martinprobson.example.ce3

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers.*
import User.*

class UserRegistrationTest extends AsyncFunSuite with AsyncIOSpec:

  object MockUserModel extends UserModel:
    override def insert(user: User): IO[User] = IO(user)
  end MockUserModel

  object MockUserNotifier extends UserNotifier:
    override def notify(user: User, msg: String): IO[String] = IO(s"Notified $user with $msg")
  end MockUserNotifier

  test("Test UserRegistration") {
    val userRegistration = UserRegistration(IO(MockUserModel), IO(MockUserNotifier))
    val user = User(1,UserName("testuser"),Email("testemail"))
    userRegistration.flatMap{ur =>
            ur.register(user)
          }.asserting{ result => result shouldBe (user,s"Notified $user with Welcome!")}
  }

end UserRegistrationTest

