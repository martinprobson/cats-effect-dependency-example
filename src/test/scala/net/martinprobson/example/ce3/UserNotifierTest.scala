package net.martinprobson.example.ce3

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import net.martinprobson.example.ce3.DB.*
import net.martinprobson.example.ce3.User.*
import net.martinprobson.example.ce3.UserModel.*
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers.*

class UserNotifierTest extends AsyncFunSuite with AsyncIOSpec:

  test("UserNotifier Test") {
    val user: User = User(1, UserName("testuser"), Email("testemail"))
    val userNotifier: IO[UserNotifier] = UserNotifier.apply
    userNotifier.flatMap { un =>
      un.notify(user,"Hello!")
    }
      .asserting(result => result shouldBe "Sending Hello! to testemail")
  }

end UserNotifierTest
