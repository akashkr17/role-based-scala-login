package com.akash.post.db

import com.akash.post.api.{GetPostRequest, Post, PostRequest, UpdatePostRequest}
import com.akash.post.db.modals.PostTable

import scala.concurrent.{ExecutionContext, Future}
import com.akash.post.db.DatabaseApi.api._
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import slick.lifted.TableQuery

class PostDAO(implicit val db: Database, schema: String, ec: ExecutionContext) {

  val userQuery = TableQuery[PostTable]
  def addPost(post: Post): Future[Int] = {
    db.run(userQuery += post)
  }
  def deletePost(postId: String): Future[Int] = {
    db.run(userQuery.filter(cr => cr.postId === postId).delete)
  }
  def getPosts(): Future[Seq[Post]] = {
    db.run(userQuery.result)
  }
  def getPostsByUserId(id: String): Future[Seq[Post]] = {
    db.run(userQuery.filter(cr =>  cr.userId === id).result)
  }
  def updatePost(updateRequest: UpdatePostRequest): Future[Int] = {
    db.run(userQuery.filter(cr =>  cr.postId === updateRequest.postId)
      .map( c => (c.text,c.title)).update(updateRequest.text,updateRequest.title))
  }
//  def checkUser(user: UserLoginRequest): Future[Int] = {
//    db.run(userQuery.filter(cr =>
//      (cr.email === user.email && cr.password === user.password)
//    ).size.result)
//  }
//  def getUser(): Future[Option[User]] = {
//    db.run(userQuery.result.headOption)
//  }
}
