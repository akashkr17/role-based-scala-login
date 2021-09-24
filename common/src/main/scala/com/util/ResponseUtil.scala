package com.util

import java.time.{Instant, OffsetDateTime}

import akka.http.scaladsl.model.{
  ContentTypes,
  HttpEntity,
  HttpResponse,
  StatusCode,
  StatusCodes
}
import com.models.{APIDataResponse, DataResponsesModels}
import com.models.DataResponsesModels.{
  EmptyData,
  Error,
  StandardResponseForCaseClass,
  StandardResponseForListCaseClass,
  StandardResponseForString,
  StandardResponseForStringError
}
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future

/**
  * Utility for handling the response formats
  */
trait ResponseUtil extends JsonHelper {

  val resourceName: String = "auth"

  def generateCommonResponse(status: Boolean,
                             error: Option[List[Error]],
                             data: Option[String] = None,
                             resource: Option[String] = Some(resourceName))
    : StandardResponseForString = {
    StandardResponseForString(resource, status, error, data)
  }

  def unauthorizedRouteResponse: Future[HttpResponse] = {
    Future.successful(
      HttpResponse(
        StatusCodes.Unauthorized,
        entity = HttpEntity(ContentTypes.`application/json`,
                            write(
                              sendFormattedError(errorCode = "ERROR01",
                                                 s"Unauthorized Access",
                                                 Some("UNAUTHORIZED_ROUTE"))))
      ))
  }

  def generateCommonResponseForCaseClass(status: Boolean,
                                         error: Option[List[Error]],
                                         data: Option[APIDataResponse] = None,
                                         resource: Option[String] = Some(
                                           resourceName))
    : StandardResponseForCaseClass = {
    StandardResponseForCaseClass(resource, status, error, data)
  }

  def generateCommonResponseForListCaseClass(
      status: String,
      error: Option[List[Error]],
      data: Option[List[APIDataResponse]] = None,
      resource: Option[String] = Some(resourceName))
    : StandardResponseForListCaseClass = {
    StandardResponseForListCaseClass(resource, status, error, data)
  }

  def sendFormattedError(errorCode: String,
                         errorMessage: String,
                         resource: Option[String] = Some(resourceName))
    : StandardResponseForStringError = {
    val error = List(Error(errorCode, errorMessage))
    generateCommonResponseForError(false, Some(error), resource = resource)
  }

  def generateCommonResponseForError(status: Boolean,
                                     error: Option[List[Error]],
                                     resource: Option[String] = Some(
                                       resourceName))
    : StandardResponseForStringError = {
    StandardResponseForStringError(resource,
                                   status,
                                   error,
                                   Some(EmptyData(None)))
  }

  def sendJsonErrorWithEmptyData(resource: Option[String] = Some(resourceName))
    : StandardResponseForStringError = {
    val error = List(Error("CAMP_003", "INVALID_JSON"))
    generateCommonResponseForError(false, Some(error), resource)
  }

  def httpResponseByStatus(statusCode: StatusCode,
                           resource: String,
                           message: String,
                           status: Boolean): HttpResponse = {
    HttpResponse(
      statusCode,
      entity = HttpEntity(ContentTypes.`application/json`,
                          write(
                            generateCommonResponse(status = status,
                                                   Some(List()),
                                                   Some(message),
                                                   Some(resource))))
    )
  }

}

object ResponseUtil extends ResponseUtil
