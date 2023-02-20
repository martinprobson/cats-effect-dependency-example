package net.martinprobson.example.ce3

sealed trait RegistrationError
case class DatabaseInsertError(u: User, msg: String) extends RegistrationError
case object EmailSendError extends RegistrationError
