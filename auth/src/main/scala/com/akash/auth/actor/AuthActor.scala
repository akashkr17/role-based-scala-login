package com.akash.auth.actor

import akka.actor.AbstractActor.Receive
import akka.actor.{AbstractActor, Props}
import akka.pattern.pipe
import com.akash.auth.api.{User, UserLoginRequest}
import com.akash.auth.db.AuthDAO
import com.models.APIDataResponse

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext.Implicits.global

class AuthActor(authDao: AuthDAO)(implicit futureAwaitDuration: FiniteDuration)
    extends FailurePropagatingActor {

  import AuthActor._

  override def receive: Receive = {
    case m@AddUserRequest(user) =>
      val res = addUser(user)
      res.pipeTo(sender())
    case m@CheckUser(user) =>
      val res = checkUser(user)
      res.pipeTo(sender())
    case m@GetUser() =>
      val res = getUser()
      res.pipeTo(sender())
  }

  def getUser(): Future[APIDataResponse] = {
    authDao.getUser().map {
      case Some(data) => UserResponse(Some(data))
      case _ => NoDataFound()
    }
  }

  def addUser(user: User): Future[AddUserResponse] = {
    authDao.addUser(user).map(c => AddUserResponse(c > 0))
  }

  def checkUser(user: UserLoginRequest): Future[APIDataResponse] = {
    authDao.checkUser(user).map {
      case None => LoginSuccess(false, None)
      case Some(user) => LoginSuccess(true, Some(user.userId))
    }
  }
}

object AuthActor {

  // commands
  sealed trait AuthActorMessage
  final case class AddUserRequest(user: User) extends AuthActorMessage
  final case class CheckUser(user: UserLoginRequest) extends AuthActorMessage
  final case class GetUser() extends AuthActorMessage
  // Replies
  sealed trait CampaignActorAck

  final case class LoginSuccess(value: Boolean,user:Option[String]) extends APIDataResponse
  final case class AddUserResponse(message: Boolean) extends APIDataResponse
  final case class UserResponse(user: Option[User]) extends APIDataResponse
  final case class NoDataFound() extends APIDataResponse


  def props(auth: AuthDAO)(
      implicit futureAwaitDuration: FiniteDuration): Props =
    Props(new AuthActor(auth))

}
