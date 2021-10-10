package com.akash.post.handler

import java.time.{Instant, OffsetDateTime}
import java.util.UUID

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.akash.post.actor.PostActor.{AddPostRequest, AddPostResponse, DeletePostRequest, DeleteSuccess, GetPostsRequest, GetPostsRequestByUserId, PostsResponse, UpdatePostRequestByPostId, UpdatePostResponse}
import com.akash.post.api.{GetPostRequest, Post, PostRequest, UpdatePostRequest}
import com.typesafe.scalalogging.LazyLogging
import com.util.ResponseUtil

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

trait PostServiceHandler extends LazyLogging with ResponseUtil{

  implicit val system: ActorSystem

  implicit val materializer: ActorMaterializer
  implicit val actionTimeOut: Timeout = {
    Timeout(40 seconds)
  }

  import akka.pattern.ask
  import system.dispatcher
  def addPost(command: ActorRef, postRequest: PostRequest): Future[HttpResponse] = {

    val post = Post(UUID.randomUUID().toString,
      postRequest.userId,
      postRequest.title,
        postRequest.text,
      OffsetDateTime.now())
    ask(command, AddPostRequest(post)).map {
      case m@AddPostResponse(true) =>

        HttpResponse(
          StatusCodes.OK,
          entity =
            HttpEntity(ContentTypes.`application/json`,
              write(
                generateCommonResponseForCaseClass(status = true,
                  Some(List()),
                  Some(m),
                  Some("ADD_POST"))))
        )
      case _ =>
        HttpResponse(
          StatusCodes.OK,
          entity =
            HttpEntity(ContentTypes.`application/json`,
              write(
                generateCommonResponseForCaseClass(status = false,
                  Some(List()),
                  None,
                  Some("Add_POST"))))
        )

    }
  }

  def updatePost(command: ActorRef,updateRequest: UpdatePostRequest): Future[HttpResponse] = {

    ask(command, UpdatePostRequestByPostId(updateRequest)).map {
      case m@UpdatePostResponse(true) =>

        HttpResponse(
          StatusCodes.OK,
          entity =
            HttpEntity(ContentTypes.`application/json`,
              write(
                generateCommonResponseForCaseClass(status = true,
                  Some(List()),
                  Some(m),
                  Some("Update_POST"))))
        )
      case _ =>
        HttpResponse(
          StatusCodes.OK,
          entity =
            HttpEntity(ContentTypes.`application/json`,
              write(
                generateCommonResponseForCaseClass(status = false,
                  Some(List()),
                  None,
                  Some("Update_POST"))))
        )

    }
  }
  def getPosts(command: ActorRef) : Future[HttpResponse] = {
    ask(command, GetPostsRequest()).map {
      case response: PostsResponse =>
        println(response.post)
        HttpResponse(StatusCodes.OK,
          entity = HttpEntity(ContentTypes.`application/json`,
            write(generateCommonResponseForCaseClass(status=true,None,data=Some(response),Some("GET_POSTS")))
          ))
      case _ => HttpResponse(StatusCodes.OK,
        entity = HttpEntity(ContentTypes.`application/json`,
          write(generateCommonResponseForCaseClass(status=false,Some(List()),data=None,Some("GET_POSTS")))
        ))
    }
  }
  def deletePost(command: ActorRef,postId: String) : Future[HttpResponse] = {
    ask(command, DeletePostRequest(postId)).map {
      case response: DeleteSuccess =>

        HttpResponse(StatusCodes.OK,
          entity = HttpEntity(ContentTypes.`application/json`,
            write(generateCommonResponseForCaseClass(status=true,Some(List()),None,Some("Delete_POSTS")))
          ))
      case _ => HttpResponse(StatusCodes.OK,
        entity = HttpEntity(ContentTypes.`application/json`,
          write(generateCommonResponseForCaseClass(status=false,Some(List()),data=None,Some("Delete_POSTS")))
        ))
    }
  }

  def getPostsByUserId(command: ActorRef, id: String) : Future[HttpResponse] = {
    ask(command, GetPostsRequestByUserId(id)).map {
      case response: PostsResponse =>
        println(response.post)
        HttpResponse(StatusCodes.OK,
          entity = HttpEntity(ContentTypes.`application/json`,
            write(generateCommonResponseForCaseClass(status=true,None,data=Some(response),Some("GET_POSTS_By_USERId")))
          ))
      case _ => HttpResponse(StatusCodes.OK,
        entity = HttpEntity(ContentTypes.`application/json`,
          write(generateCommonResponseForCaseClass(status=false,Some(List()),data=None,Some("GET_POSTS_By_USERId")))
        ))
    }
  }



}
