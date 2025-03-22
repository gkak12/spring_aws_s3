package com.spring.aws.s3.common;

import com.spring.aws.s3.domain.dto.AwsS3RequestDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
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

    public void putObject(AwsS3RequestDto awsS3RequestDto) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(awsS3RequestDto.getBucketName())
                .key(awsS3RequestDto.getFileName())
                .build();

            PutObjectResponse response = this.s3Client.putObject(
                putObjectRequest
                , RequestBody.fromBytes(awsS3RequestDto.getMultipartFile().getBytes())
            );

            int statusCode = response.sdkHttpResponse().statusCode();

            if(statusCode == 200 || statusCode == 204){
                log.info("SUCCESS: AWS S3 upload object to S3.");
            } else {
                log.error("ERROR: AWS S3 upload object to S3.");
                throw new RuntimeException("ERROR: AWS S3 upload object to S3.");
            }
        } catch (AccessDeniedException e) {
            log.error("ERROR: AccessDeniedException.");
            throw new RuntimeException("ERROR: AccessDeniedException.", e);
        } catch (IOException e) {
            log.error("ERROR: IOException.");
            throw new RuntimeException("ERROR: IOException.", e);
        } catch (NoSuchBucketException e) {
            log.error("ERROR: NoSuchBucketException.");
            throw new RuntimeException("ERROR: NoSuchBucketException.", e);
        } catch (S3Exception e) {
            log.error("ERROR: S3Exception.");
            throw new RuntimeException("ERROR: S3Exception.", e);
        } catch (SdkServiceException e) {
            log.error("ERROR: SdkServiceException.");
            throw new RuntimeException("ERROR: SdkServiceException.", e);
        } catch (SdkClientException e) {
            log.error("ERROR: SdkClientException.");
            throw new RuntimeException("ERROR: SdkClientException.", e);
        } catch (Exception e) {
            log.error("ERROR: Exception.");
            throw new RuntimeException("ERROR: Exception.", e);
        }
    }

    public Resource getObject(AwsS3RequestDto awsS3RequestDto) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsS3RequestDto.getBucketName())
                .key(awsS3RequestDto.getFileName())
                .build();

            ResponseInputStream<GetObjectResponse> s3ObjectStream = s3Client.getObject(getObjectRequest);
            return new InputStreamResource(s3ObjectStream);
        } catch (NoSuchKeyException e) {
            throw new RuntimeException("ERROR: 요청한 파일이 존재하지 않습니다.", e);
        } catch (SdkServiceException e) {
            throw new RuntimeException("ERROR: AWS 서비스 오류 발생했습니다.", e);
        } catch (Exception e) {
            log.error("ERROR: Exception");
            throw new RuntimeException("ERROR: Exception", e);
        }
    }

    public boolean deleteObject(AwsS3RequestDto awsS3RequestDto) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(awsS3RequestDto.getBucketName())
                .key(awsS3RequestDto.getFileName())
                .build();

            DeleteObjectResponse response = s3Client.deleteObject(deleteObjectRequest);
            return response.sdkHttpResponse().statusCode() == 204 ? true : false;
        } catch (NoSuchKeyException e) {
            throw new RuntimeException("ERROR: 삭제하려는 파일이 존재하지 않습니다.", e);
        } catch (SdkServiceException e) {
            throw new RuntimeException("ERROR: AWS 서비스 오류 발생했습니다.", e);
        } catch (Exception e) {
            log.error("ERROR: Exception");
            throw new RuntimeException("ERROR: Exception", e);
        }
    }
}
