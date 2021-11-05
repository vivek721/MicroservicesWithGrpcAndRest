package com.vivek.grpc

import HelperUtils.CreateLogger
import com.cs441.vivek.timecheck.{TimeRequest, TimeResponse, TimeServiceGrpc}
import com.typesafe.config.{Config, ConfigFactory}
import com.vivek.rest.RestClient
import scalaj.http.Http

import java.util.Base64
import scala.concurrent.Future

/**
 * This is grpc client that calls the lambda function deployed on aws server
 *
 * All the configuration are read from application.conf file.
 *
 * The code entry point is the RunClient.scala.
 *
 * @author Vivek Mishra
 *
 */
class TimeClientImpl extends TimeServiceGrpc.TimeService {
  override def checkTime(timeRequest: TimeRequest): Future[TimeResponse] = {
    val config: Config = ConfigFactory.load("application" + ".conf")
    val logger = CreateLogger(classOf[RestClient])

    // sending request to grpc server on lambda
    val request = Http(config.getString("Grpc.apiUrl")).headers(Map(
      "Content-Type" -> "application/grpc+proto",
      "Accept" -> "application/grpc+proto"
    )).timeout(config.getInt("Rest.connTimeoutMin"), config.getInt("Rest.readTimeoutMin"))
      .postData(timeRequest.toByteArray)

    val response = request.asBytes

    val output = Base64.getDecoder.decode(response.body)
    val result = TimeResponse.parseFrom(output)

    logger.info(result.isPresent)

    Future.successful(TimeResponse(isPresent = ""))
  }
}
