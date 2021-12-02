import com.typesafe.config.ConfigFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class TimeClientTest extends AnyFlatSpec with Matchers {
  val config = ConfigFactory.load("application" + ".conf")
  it should "RegEx should length > 0" in {
    val logTime = config.getString("Rest.logTime")
    assert(logTime.length > 0)
  }

  it should "logType should of type ERROR" in {
    val logType = config.getString("Rest.logType")
    assert(logType == "ERROR")
  }

  it should "Rest.apiUrl should be present" in {
    val apiUrl = config.getString("Rest.apiUrl")
    assert(apiUrl.length > 0)
  }

  it should "Grpc.apiUrl should length > 0" in {
    val logTime = config.getString("Grpc.logTime")
    assert(logTime.length > 0)
  }

  it should "logTime should be of format HH.mm.ss.SSS" in {
    val logTime = config.getString("Grpc.logTime")
    val substr = logTime.split(":")
    assert(substr.length == 3)
  }

}