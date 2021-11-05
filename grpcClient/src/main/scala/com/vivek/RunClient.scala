package com.vivek

import com.cs441.vivek.timecheck.TimeRequest
import com.vivek.grpc.TimeClientImpl
import com.vivek.rest.RestClient

/** Main Method - Triggers the MapReduce method based on command line input
 *
 * @param args : Array[String] - command line input args(0) -> grpc(for grpc client) or rest(for rest client)
 */

object RunClient {
  def main(args: Array[String]): Unit = {
    args(0) match {
      // to run the grpc client
      case "grpc" => {
        val timeClientImpl = new TimeClientImpl();
        val obj = TimeRequest("05:01:27.882")
        timeClientImpl.checkTime(obj);
      }
      // to run the rest client
      case "rest" => {
        val restClient = new RestClient()
        restClient.sendRequest()
      }
    }
  }
}
