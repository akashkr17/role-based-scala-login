package com.akash.auth.db.modals

import java.time.Instant

import com.akash.auth.api.User
import slick.lifted.ProvenShape
import com.akash.auth.db.DatabaseApi.api._

final class UserTable(tag: Tag)(implicit val schema: String)
    extends Table[User](tag, Some("auth"), "user_login") {

  def userId: Rep[String] = column[String]("user_id")
  def email: Rep[String] = column[String]("email")
  def password: Rep[String] = column[String]("password")
  def name: Rep[String] = column[String]("name")
  def number: Rep[String] = column[String]("number")

  def createdAt: Rep[Instant] = column[Instant]("created_at")

  //noinspection ScalaStyle
  def * : ProvenShape[User] =
    (
      userId,
      email,
      password,
      name,
      number,
      createdAt
    ).shaped <> (User.tupled, User.unapply)
}
