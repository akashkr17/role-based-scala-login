package com.akash.post.db.modals

import java.time.{Instant, OffsetDateTime}

import com.akash.post.api.Post
import slick.lifted.ProvenShape
import com.akash.post.db.DatabaseApi.api._

final class PostTable(tag: Tag)(implicit val schema: String)
  extends Table[Post](tag, Some("post"), "post") {
  def postId: Rep[String] = column[String]("post_id")
  def userId: Rep[String] = column[String]("user_id")
  def title: Rep[String] = column[String]("title")
  def text: Rep[String] = column[String]("text")
  def createdAt: Rep[OffsetDateTime] = column[OffsetDateTime]("created_at")

  //noinspection ScalaStyle
  def * : ProvenShape[Post] =
    ( postId,
      userId,
      title,
      text,
      createdAt
      ).shaped <> (Post.tupled, Post.unapply)
}
