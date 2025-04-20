package kz.jarkyn.backend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

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
    public S3Client getS3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(s3Endpoint))
                .region(Region.of(s3Region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(s3Key, s3Secret)))
                .serviceConfiguration(s3 -> s3.pathStyleAccessEnabled(true))
                .build();
    }
}