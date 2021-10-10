package com.akash.post.actor

import java.time.{Instant, OffsetDateTime, ZoneId}

import akka.actor.AbstractActor.Receive
import akka.actor.{AbstractActor, Props}
import akka.pattern.pipe
import com.akash.post.actor.FailurePropagatingActor
import com.akash.post.api.{GetPostRequest, Post, PostRequest, UpdatePostRequest}
import com.akash.post.db.PostDAO
import com.models.APIDataResponse

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext.Implicits.global

class PostActor(postDao: PostDAO)(implicit futureAwaitDuration: FiniteDuration)
  extends FailurePropagatingActor {
  import PostActor._
  override def receive: Receive = {
    case m @ AddPostRequest(post) =>
      val res = addPost(post)
      res.pipeTo(sender())
    case m @ UpdatePostRequestByPostId(updateRequest) =>
      val res = updatePost(updateRequest)
      res.pipeTo(sender())
    case m @ GetPostsRequestByUserId(postRequest) =>
      val res = getPostsByUserId(postRequest)
      res.pipeTo(sender())
    case m @ GetPostsRequest() =>
      val res = getPosts()
      res.pipeTo(sender())
    case m @ DeletePostRequest(postId) =>
      val res = deletePost(postId)
      res.pipeTo(sender())
  }

  def deletePost(postId:String): Future[CampaignActorAck] = {
    postDao.deletePost(postId).map(c => DeleteSuccess(c > 0))
  }

  def addPost(post: Post): Future[APIDataResponse] = {
    postDao.addPost(post).map(c => AddPostResponse(c > 0))
  }
  def updatePost(postRequest: UpdatePostRequest): Future[APIDataResponse] = {
    postDao.updatePost(postRequest).map(c => UpdatePostResponse(c > 0))
  }

  def getPosts():Future[APIDataResponse] = {
    postDao.getPosts().map {
      case Nil => NoDataFound()
      case data =>
      {
        println(data)
        PostsResponse(data, OffsetDateTime.ofInstant(Instant.now,ZoneId.of("UTC")))
      }
    }
  }
  def getPostsByUserId(postRequest: String):Future[APIDataResponse] = {
    postDao.getPostsByUserId(postRequest).map {
      case Nil => NoDataFound()
      case data =>
      {
        println(data)
        PostsResponse(data, OffsetDateTime.ofInstant(Instant.now,ZoneId.of("UTC")))
      }
    }
  }

}

object PostActor {

  // commands
  sealed trait PostActorMessage
  final case class AddPostRequest(post: Post) extends PostActorMessage
  final case class UpdatePostRequestByPostId(updateRequest: UpdatePostRequest) extends PostActorMessage
  final case class GetPostsRequest() extends PostActorMessage
  final case class DeletePostRequest(postId: String) extends PostActorMessage
  final case class GetPostsRequestByUserId(post: String) extends PostActorMessage
  // Replies
  sealed trait CampaignActorAck

  final case class DeleteSuccess(value: Boolean) extends CampaignActorAck
  final case class AddPostResponse(message: Boolean) extends APIDataResponse
  final case class UpdatePostResponse(message: Boolean) extends APIDataResponse
  final case class PostsResponse(post: Seq[Post], time: OffsetDateTime) extends APIDataResponse
  final case class NoDataFound() extends APIDataResponse


  def props(post: PostDAO)(
    implicit futureAwaitDuration: FiniteDuration): Props =
    Props(new PostActor(post))

}
