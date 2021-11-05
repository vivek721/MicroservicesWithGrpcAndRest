import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetObject2 {

    public ArrayList<String> readFile(LambdaLogger logger, String inputTime, String delta, String logType) throws IOException {

        Regions clientRegion = Regions.DEFAULT_REGION;
        String bucketName = "mylogawsbucket";
        String key = "logfolder/LogFileGenerator.2021-11-01.log";
        ArrayList<String> result = new ArrayList<>();

        S3Object fullObject = null, objectPortion = null, headerOverrideObject = null;
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion("us-east-2")
                    .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                    .build();

            // Get an object and print its contents.
            logger.log("Downloading an object");
            fullObject = s3Client.getObject(new GetObjectRequest(bucketName, key));
            logger.log("Content-Type: " + fullObject.getObjectMetadata().getContentType());
            logger.log("Content: ");
            result = displayTextInputStream(fullObject.getObjectContent(), inputTime, delta, logType);

        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        } finally {
            // To ensure that the network connection doesn't remain open, close any open input streams.
            if (fullObject != null) {
                fullObject.close();
            }
            if (objectPortion != null) {
                objectPortion.close();
            }
            if (headerOverrideObject != null) {
                headerOverrideObject.close();
            }
        }
        return result;
    }

    private static ArrayList<String> displayTextInputStream(InputStream input, String inputTime, String deltaTime, String logType)
            throws IOException {
        ArrayList<String> result = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;
        LocalTime logTime = LocalTime.parse(inputTime);
        LocalTime delta = LocalTime.parse(deltaTime);
        LocalTime endTime = logTime.plusHours(delta.getHour()).plusMinutes(delta.getMinute())
                .plusSeconds(delta.getSecond()).plusNanos(delta.getNano());
        LocalTime startTime = logTime.minusHours(delta.getHour()).minusMinutes(delta.getMinute())
                .minusSeconds(delta.getSecond()).minusNanos(delta.getNano());
        boolean checkTime;
        while ((line = reader.readLine()) != null) {
            String parsedLogTime = line.split("\\s+")[0];
            checkTime = (
                    LocalTime.parse(parsedLogTime).isAfter(startTime)
                            &&
                            LocalTime.parse(parsedLogTime).isBefore(endTime));
            Pattern pattern = Pattern.compile(logType);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find() && checkTime) {
                result.add(line);
            }
        }
        return result;
    }
}