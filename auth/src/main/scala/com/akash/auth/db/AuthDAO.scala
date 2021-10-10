package com.akash.auth.db

import com.akash.auth.api.{User, UserLoginRequest}
import com.akash.auth.db.modals.UserTable

import scala.concurrent.{ExecutionContext, Future}
import DatabaseApi.api._
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import slick.lifted.TableQuery

class AuthDAO(implicit val db: Database, schema: String, ec: ExecutionContext) {

  val userQuery = TableQuery[UserTable]
  def addUser(user: User): Future[Int] = {
    db.run(userQuery += user)
  }
  def checkUser(user: UserLoginRequest): Future[Option[User]] = {
    db.run(userQuery.filter(cr =>
      (cr.email === user.email && cr.password === user.password)
    ).result.headOption)
  }
  def getUser(): Future[Option[User]] = {
    db.run(userQuery.result.headOption)
  }
}
