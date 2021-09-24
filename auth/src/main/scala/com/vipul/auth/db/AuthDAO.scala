package com.vipul.auth.db

import com.vipul.auth.api.User

import scala.concurrent.{ExecutionContext, Future}
import com.vipul.auth.db.DatabaseApi.api._
import com.vipul.auth.db.modals.UserTable
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import slick.lifted.TableQuery

class AuthDAO(implicit val db: Database, schema: String, ec: ExecutionContext) {

  val userQuery = TableQuery[UserTable]
  def addUser(user: User): Future[Int] = {
    db.run(userQuery += user)
  }
}
