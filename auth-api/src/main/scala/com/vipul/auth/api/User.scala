package com.vipul.auth.api

import java.time.Instant

case class User(userId: String,
                email: String,
                password: String,
                createdAt: Instant)

case class UserRequest(email: String, password: String)
