package com.akash.auth.api

import java.time.Instant

case class User(userId: String,
                email: String,
                password: String,
                name: String,
                number: String,
                createdAt: Instant)

case class UserRequest(email: String, password: String,name: String,number: String)
case class UserLoginRequest(email: String, password: String)