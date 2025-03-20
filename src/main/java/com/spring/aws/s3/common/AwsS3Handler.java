package com.spring.aws.s3.common;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

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
}
