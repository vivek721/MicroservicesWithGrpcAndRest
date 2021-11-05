package com.vivek.rest

import HelperUtils.CreateLogger
import com.google.gson.Gson
import com.typesafe.config.{Config, ConfigFactory}
import scalaj.http.Http

/**
 * This is rest client that calls the lambda function deployed on aws server
 *
 * All the configuration are read from application.conf file.
 *
 * The code entry point is the RunClient.scala.
 *
 * @author Vivek Mishra
 *
 */

class RestClient {
  /**
   * This method sends the request to the lambda function on aws
   */

  def sendRequest(): Unit = {
    val config: Config = ConfigFactory.load("application" + ".conf")
    val logger = CreateLogger(classOf[RestClient])

    // Create case class object

    val logTime = RequestClass(config.getString("Rest.logTime"), config.getString("Rest.delta")
      , config.getString("Rest.logType"))
    val gson = new Gson()
    val timeJson = gson.toJson(logTime)

    // Http request to the aws lambda function that returns the required logs
    val request = Http(config.getString("Rest.apiUrl")).headers(Map(
      "Content-Type" -> "application/json",
      "Accept" -> "application/json"
    )).timeout(config.getInt("Rest.connTimeoutMin"), config.getInt("Rest.readTimeoutMin"))
      .postData(timeJson)

    //output of the response body from rest lambda server
    val output = request.asString.body

    logger.info(output)
  }

  // case class to define type of request
  case class RequestClass(time: String, delta: String, logType: String)
}
