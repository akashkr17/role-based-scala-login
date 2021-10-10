package com.akash.post.api

import java.time.{Instant, OffsetDateTime}

case class Post( postId: String,
                 userId: String,
                title: String,
                text: String,
                createdAt: OffsetDateTime)

case class PostRequest(userId: String, title: String,text: String)
case class UpdatePostRequest(postId:String,title: String,text: String)
case class GetPostRequest(userId: String)