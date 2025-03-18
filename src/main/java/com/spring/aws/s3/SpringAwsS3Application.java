package com.spring.aws.s3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class SpringAwsS3Application {
    public static void main(String[] args) {
        SpringApplication.run(SpringAwsS3Application.class, args);
    }
}