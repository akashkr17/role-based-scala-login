package com.util

object Role extends Enumeration {
  type Section = Value
  val SUPER = Value("Super")
  val ADMIN = Value("Admin")
  val VISITOR = Value("Visitor")
  val USER = Value("User")
  val Guest = Value

  def isUser(s: String): Boolean = values.exists(_.toString == s)
  def withGuest(name: String): Value =
    values
      .find(_.toString.toLowerCase() == name.toLowerCase())
      .getOrElse(Guest)
}
