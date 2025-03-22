package com.spring.aws.s3.common;

import com.spring.aws.s3.domain.dto.RequestDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class AwsS3Handler {

    private S3Client s3Client;

    @PostConstruct
    public void init(){
        S3ClientBuilder builder = S3Client.builder().region(Region.of("amazon.s3.region"));
        String roleArn = System.getenv("ROLE_ARN");
        String tokenFile = System.getenv("TOKEN_FILE");

        if(StringUtils.isEmpty(roleArn) || StringUtils.isEmpty(tokenFile)){
            log.info("roleArn or tokenFile is null.");
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        } else {
            Path tokenFilePath = Paths.get(tokenFile);

            if(Files.notExists(tokenFilePath) || !Files.isReadable(tokenFilePath)){
                throw new RuntimeException("tokenFilePath is not existed or not readable.");
            }

            WebIdentityTokenFileCredentialsProvider credentialsProvider = WebIdentityTokenFileCredentialsProvider.builder()
                    .roleArn(roleArn)
                    .webIdentityTokenFile(tokenFilePath)
                    .build();

            builder.credentialsProvider(credentialsProvider);
        }

        this.s3Client = builder.build();
    }

    public void putObject(RequestDto requestDto) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(requestDto.getBucketName())
                    .key(requestDto.getFileName())
                    .build();

            PutObjectResponse response = this.s3Client.putObject(
                putObjectRequest
                , RequestBody.fromBytes(requestDto.getMultipartFile().getBytes())
            );

            int statusCode = response.sdkHttpResponse().statusCode();

            if(statusCode == 200 || statusCode == 204){
                log.info("SUCCESS: AWS S3 upload object to S3.");
            } else {
                log.error("ERROR: AWS S3 upload object to S3.");
                throw new RuntimeException("ERROR: AWS S3 upload object to S3.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Resource getObject(String bucket, String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        ResponseInputStream<GetObjectResponse> s3ObjectStream = s3Client.getObject(getObjectRequest);
        return new InputStreamResource(s3ObjectStream);
    }

    public boolean deleteObject(String bucket, String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        DeleteObjectResponse response = s3Client.deleteObject(deleteObjectRequest);

        boolean res = response.sdkHttpResponse().statusCode() == 204 ? true : false;
        return res;
    }
}
