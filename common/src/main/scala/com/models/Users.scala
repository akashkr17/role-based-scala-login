package com.models

import java.time.Instant

case class UserLogin(email: String,
                     password: String,
                     createdAt: Instant,
                     updatedAt: Instant,
                     role: String)
case class Users(nickname: String,
                 gender: String,
                 emailId: String,
                 campaignSlug: String,
                 userId: String)

case class EmailWithToken(
    email: String,
    token: String,
    role: String
)

final case class AdminTokenParam(email: String, role: String)

case class JWTTokenExtracts(exp: Long, iat: Long, email: String, role: String)
