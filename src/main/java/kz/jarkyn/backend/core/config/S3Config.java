package kz.jarkyn.backend.core.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.immutables.value.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${cloud.s3.key}")
    private String s3Key;
    @Value("${cloud.s3.secret}")
    private String s3Secret;
    @Value("${cloud.s3.endpoint}")
    private String s3Endpoint;
    @Value("${cloud.s3.region}")
    private String s3Region;

    @Bean
    public AmazonS3 getS3() {
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(s3Endpoint, s3Region))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(s3Key, s3Secret)))
                .withPathStyleAccessEnabled(true).build();
    }
}