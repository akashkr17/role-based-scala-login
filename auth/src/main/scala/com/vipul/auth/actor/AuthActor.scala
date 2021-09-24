package com.vipul.auth.actor

import akka.actor.AbstractActor.Receive
import akka.actor.Props
import akka.pattern.pipe
import com.models.APIDataResponse
import com.vipul.auth.api.User
import com.vipul.auth.db.AuthDAO

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext.Implicits.global

class AuthActor(authDao: AuthDAO)(implicit futureAwaitDuration: FiniteDuration)
    extends FailurePropatingActor {
  import AuthActor._
  override def receive: Receive = {
    case m @ AddUserRequest(user) =>
      val res = addUser(user)
      res.pipeTo(sender())
  }
  def addUser(user: User): Future[AddUserResponse] = {
    authDao.addUser(user).map(c => AddUserResponse(c > 0))
  }

}

object AuthActor {

  // commands
  sealed trait AuthActorMessage
  final case class AddUserRequest(user: User) extends AuthActorMessage
  // Replies
  sealed trait CampaignActorAck

  final case class AddUserResponse(message: Boolean) extends APIDataResponse

  def props(auth: AuthDAO)(
      implicit futureAwaitDuration: FiniteDuration): Props =
    Props(new AuthActor(auth))

}
