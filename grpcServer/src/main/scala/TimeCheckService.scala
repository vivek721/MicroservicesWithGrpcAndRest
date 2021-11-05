import com.cs441.vivek.timecheck.TimeServiceGrpc.TimeService
import com.cs441.vivek.timecheck.{TimeRequest, TimeResponse}

import scala.concurrent.Future

object TimeCheckService extends TimeService {
  override def checkTime(request: TimeRequest): Future[TimeResponse] = {
    val logTime = request.time
    val getObject2 = new GetObject2()
    val result = getObject2.readFile(logTime)

    val response = TimeResponse(result.toString)

    // Send the result as response
    Future.successful(response)
  }
}
