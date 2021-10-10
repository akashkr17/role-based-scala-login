/*
 * © Copyright 2019 The Globe and Mail
 */

package com.akash.post.actor

import akka.actor.{Actor, Status}

/**
  * The idea is from: https://medium.com/@linda0511ny/error-handling-in-akka-actor-with-future-ded3da0579dd
  * How it works: When there is a failure or exception, the actor gets restarted and the exception doesn't propagate
  * back to the Sender. Using this technique, we send back the exception back to the sender to deal with it.
  *
  */
trait FailurePropagatingActor extends Actor {
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    super.preRestart(reason, message)
    sender() ! Status.Failure(reason)
  }
}
