import com.amazonaws.services.lambda.runtime.events.{APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent}
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.cs441.vivek.timecheck.TimeRequest

import java.util.Base64
import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

/**
 * This is grpc server that deployed as lambda on aws server
 *
 * All the configuration are read from application.conf file.
 *
 * @author Vivek Mishra
 *
 */
class TimeCheckImpl extends RequestHandler[APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent] {
  override def handleRequest(input: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent = {
    // Get AWS Lambda Logger
    val logger = context.getLogger
    logger.log("Request Body:\n" + input.toString)

    // Decode base-64 encoded binary data from the request body
    val message = if (input.getIsBase64Encoded) Base64.getDecoder.decode(input.getBody.getBytes) else input.getBody.getBytes
    logger.log(s"message: (${message.mkString(", ")})")

    // Construct the expression from binary data
    val timeRequest = TimeRequest.parseFrom(message)
    logger.log(s"Expression: $timeRequest")

    val result = Await.result(TimeCheckService.checkTime(timeRequest), atMost = 5 seconds)

    val output = Base64.getEncoder.encodeToString(result.toByteArray)

    //APIGatewayProxyResponseEvent response to the client
    new APIGatewayProxyResponseEvent()
      .withStatusCode(200)
      .withHeaders(Map("Content-Type" -> "application/grpc+proto").asJava)
      .withBody(output)
  }
}
