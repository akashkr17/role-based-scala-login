package com.models

import java.time.{Instant, OffsetDateTime}

trait APIDataResponse

object DataResponsesModels {

  case class StandardResponseForString(
      resource: Option[String],
      status: Boolean,
      errors: Option[List[Error]],
      data: Option[String]
  )

  case class StandardResponseForAdminLogin(username: String,
                                           status: Boolean,
                                           accessToken: String,
                                           message: String,
                                           createdAt: OffsetDateTime,
                                           expiresIn: Long,
                                           flag: Boolean)

  case class EmptyData(data: Option[String])

  case class StandardResponseForStringError(
      resource: Option[String],
      status: Boolean,
      errors: Option[List[Error]],
      data: Option[EmptyData]
  )

  case class StandardResponseForCaseClass(
      resource: Option[String],
      status: Boolean,
      errors: Option[List[Error]],
      data: Option[APIDataResponse]
  )

  case class StandardResponseForListCaseClass(
      resource: Option[String],
      status: String,
      errors: Option[List[Error]],
      data: Option[List[APIDataResponse]]
  )

  case class Error(
      id: String,
      message: String,
      fields: Option[String] = None
  )

}
