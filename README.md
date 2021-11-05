# CS 441 - Engineering Distributed Objects for Cloud Computing
## Homework 6 - Lambda gRPC Calculator

---

### Overview

The objective of this homework is to create a gRPC service to check if the log for a given time is present in the logfile which is deployed on AWS Lambda.
We have to create another service using REST service which is also deployed on AWS lambda that checks if for log message in a given time interval having given regular expression.
The logfile generator program runs on EC2 server and the logfile in stored in S3 bucket.

### Project Structure

This project uses SBT multi-project build system and consists of the following sub-projects:

1. **grpcRestClient:** Holds the implementation of the Client for gRPC and Rest services. *Grpc depends on `protobuf` which serializes the data*.
    
2. **grpcServer:** AWS Lambda Function that uses Protobuf as the data-interchange format which serializes the data and deployed on AWS lambda.
    - grpcRestClient is deployed on AWS Lambda that responds to gRPC client calls. Service provides the implementation of the gRPC service that is defined using protobuf.
      The input to the lambda function is *APIGatewayProxyRequestEvent* and the response is *APIGatewayResponseEvent*.
      API Gateway must be configured to pass binary data in the request body of the client by configuring the Binary Media Types settings of the API to application/grpc+proto.
      For deploying this function to AWS Lambda, we just need to issue the command sbt assembly to package it into a fat jar and upload it on AWS Lambda.

3. **restServer:** As the name suggests it is a Rest Project for AWS Lambda Function that uses JSON as the data-interchange format.
    - grpcServer is deployed on AWS Lambda that responds to Rest client calls. The input to the lambda function is a Map<String, String> object and the output is a ArrayList<String> object.
      For deploying this function to AWS Lambda, we just need to issue the command sbt assembly to package it into a fat jar and upload it on AWS Lambda.

#### Protobuf 

The timecheck.proto file defines the gRPC service al shown below:

```text
syntax = "proto3";

package com.cs441.vivek;

service TimeService {
  rpc CheckTime(TimeRequest) returns (TimeResponse);
}

message TimeRequest {
  string time = 1;
}

message TimeResponse {
  string isPresent = 1;
}
```

### Sample Rest API request and response

***Rest***
**Sample Request in JSON**
```json
{
  "time": "05:01:27.882",
  "delta": "00:00:03.000",
  "logType": "ERROR"
}
```
***Rest***
**Sample Response**

```
[
  "05:01:25.096 [scala-execution-context-global-65] ERROR HelperUtils.Parameters$ - ihu}!A2]*07}|,lc",
  "05:01:26.272 [scala-execution-context-global-65] ERROR HelperUtils.Parameters$ - :9rJ_dcIhK~j0qVFS;"
]
```


### Setup Instructions
### Prerequisites to build and run the project

- [SBT](https://www.scala-sbt.org/) installed on your system
- [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html) installed and configured on your system

#### Deploying gRPC functions on AWS Lambda

1. Clone the repository from github
    ```text
    > git@github.com:vivek721/MicroservicesWithGrpcAndRest.git
    ```

2. Move to grpcServer Directory and execute below commands
   ```text
   > cd grpcServer
   sbt clean compile assembly
   ```
3. Deploy the jar into AWS lambda following the video instructions provided.



#### Deploying REST functions on AWS Lambda

1. Clone the repository from github
    ```text
    > git@github.com:vivek721/MicroservicesWithGrpcAndRest.git
    ```

2. Move to restServer Directory and execute below commands
   ```text
   > cd restServer
   sbt clean compile assembly
   ```
3. Deploy the jar into AWS lambda following the video instructions provided.
4. Open command prompt (if on Windows) or terminal (if on Mac/Linux)
 ```text
 aws apigateway update-integration-response --rest-api-id <rest api id> --resource-id <Resource ID> --http-method POST --status-code 200 --patch-operations op='replace',path='/contentHandling'
```
5. In your browser, from the Actions dropdown, select Deploy API
6. Choose Deployment stage as "New Stage" and Stage Name as prod and click on Deploy button
7. Your API is now deployed at the URL mentioned in prod Stage Editor page.

#### To run the client program

1. Clone the repository from github
    ```text
    > git@github.com:vivek721/MicroservicesWithGrpcAndRest.git
    ```
2. Move to grpcRestClient Directory and execute below commands
   ```text
   > cd grpcRestClient
   sbt clean compile 
   ```
3. To run the rRPC client execute the below command in grpcRestClient directory
    ```text
    > sbt run grpc 
   ```
4. To run the Rest client execute the below command in grpcRestClient directory
    ```text
    > sbt run rest 
   ```
