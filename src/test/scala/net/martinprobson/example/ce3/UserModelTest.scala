package net.martinprobson.example.ce3

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers.*
import User.*
import UserModel.*

class UserModelTest extends AsyncFunSuite with AsyncIOSpec:

  test("UserModel Test") {
    val user: User = User(1, UserName("testuser"), Email("testemail"))
    val userModel: IO[UserModel] = UserModel.apply(InMemoryDB.empty)
    userModel
      .flatMap { um =>
        um.insert(user)
      }
      .asserting(u => u shouldBe user)
  }

end UserModelTest
