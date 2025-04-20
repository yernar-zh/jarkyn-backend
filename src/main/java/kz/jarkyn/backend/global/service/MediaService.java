package kz.jarkyn.backend.global.service;

import io.minio.MinioClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class MediaService {

    private final S3Client s3Client;

    public MediaService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        MinioClient minioClient = null;

        String key = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String bucketName = "test1";
        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName).key(key).acl("public-read")
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
            return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(key)).toString();
        }
    }
}