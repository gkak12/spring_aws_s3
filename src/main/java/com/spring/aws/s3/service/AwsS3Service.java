package com.spring.aws.s3.service;

import com.spring.aws.s3.domain.dto.AwsS3RequestDto;
import org.springframework.core.io.Resource;

public interface AwsS3Service {

    void putObject(AwsS3RequestDto awsS3RequestDto);
    Resource getObject(AwsS3RequestDto awsS3RequestDto);
    void deleteObject(AwsS3RequestDto awsS3RequestDto);
}
