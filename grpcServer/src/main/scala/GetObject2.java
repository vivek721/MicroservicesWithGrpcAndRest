import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalTime;

public class GetObject2 {

    public boolean readFile(String inputTime) throws IOException {
        Config config = ConfigFactory.load("application" + ".conf");
        String bucketName = config.getString("Grpc.bucketName");
        String key = config.getString("Grpc.key");
        boolean result = false;

        S3Object fullObject = null, objectPortion = null, headerOverrideObject = null;
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(config.getString("Rest.region"))
                    .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                    .build();

            // Get an object and print its contents.

            fullObject = s3Client.getObject(new GetObjectRequest(bucketName, key));
            fullObject.getObjectMetadata().getContentType();
            result = displayTextInputStream(fullObject.getObjectContent(), inputTime);

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

    private static boolean displayTextInputStream(InputStream input, String inputTime) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = null;
        while ((line = reader.readLine()) != null) {
            String logTime = line.split("\\s+")[0];
            if(LocalTime.parse(logTime).compareTo(LocalTime.parse(inputTime)) == 0){
                return true;
            }
        }
        return false;
    }
}
