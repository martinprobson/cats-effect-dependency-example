package net.martinprobson.example.ce3

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers.*

import User.*

class UserNotifierTest extends AsyncFunSuite with AsyncIOSpec:

  test("UserNotifier Test") {
    val user: User = User(1, UserName("testuser"), Email("testemail"))
    (for {
      emailService <- EmailService.apply
      userNotifier <- UserNotifier(emailService)
      r <- userNotifier.notify(user, "Hello!")
    } yield r)
      .asserting(result => result shouldBe "Sending Hello! to testemail")
  }

end UserNotifierTest
