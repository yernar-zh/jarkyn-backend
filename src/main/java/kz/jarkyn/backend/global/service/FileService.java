package kz.jarkyn.backend.global.service;

import kz.jarkyn.backend.global.model.dto.FileDto;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
public class FileService {
    private static final String bucketName = "main";

    private final S3Client s3Client;

    public FileService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public UUID upload(FileDto file) {
        UUID id = UUID.randomUUID();
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName).key(id.toString())
                .contentType(file.getContentType())
                .contentDisposition(file.getContentDisposition())
                .build();
        s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return id;
    }

    public FileDto downloadFile(UUID id) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName).key(id.toString())
                .build();
        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getRequest);
        GetObjectResponse metadata = s3Object.response();
        return new FileDto(s3Object, metadata.contentDisposition(), metadata.contentType(), metadata.contentLength());
    }
}