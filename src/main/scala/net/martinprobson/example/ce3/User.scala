package net.martinprobson.example.ce3

import User.*

case class User(id: USER_ID, name: UserName, email: Email)

object User:
  opaque type UserName = String
  object UserName:
    def apply(name: String): UserName = name

  opaque type Email = String
  object Email:
    def apply(email: String): Email = email

  def apply(id: USER_ID, name: UserName, email: Email): User =
    new User(id, name, email)
  def apply(name: UserName, email: Email): User =
    User(UNASSIGNED_USER_ID, name, email)

  type USER_ID = Long
  val UNASSIGNED_USER_ID = 0L

end User
