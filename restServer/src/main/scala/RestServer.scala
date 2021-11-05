import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.google.gson.GsonBuilder

import java.util

/**
 * This is Rest Server deployed on lambda on aws server
 *
 * All the configuration are read from application.conf file.
 *
 * @author Vivek Mishra
 *
 */
class RestServer extends RequestHandler[util.Map[String, String], util.ArrayList[String]] {
  val gson = new GsonBuilder().setPrettyPrinting().create()

  override def handleRequest(input: util.Map[String, String], context: Context): util.ArrayList[String] = {
    // Get AWS Lambda Logger
    val logger = context.getLogger
    logger.log("Request Body:\n" + input)
    val logTime = input.get("time")
    val delta = input.get("delta")
    val logType = input.get("logType")

    // Creating getObject2 object this retrieves S3 object
    val getObject2 = new GetObject2()

    //Result returns the result to client
    val result = getObject2.readFile(logger, logTime, delta, logType)
    logger.log("Input: " + gson.toJson(input));
    logger.log("Input: " + gson.toJson(input.get("Time")));
    result
  }
}
