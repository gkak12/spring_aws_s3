package com.spring.aws.s3.service.impl;

import com.spring.aws.s3.common.AwsS3Handler;
import com.spring.aws.s3.domain.dto.AwsS3RequestDto;
import com.spring.aws.s3.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3ServiceImpl implements AwsS3Service {

    private final AwsS3Handler awsS3Handler;

    @Override
    public void putObject(AwsS3RequestDto awsS3RequestDto) {
        awsS3Handler.putObject(awsS3RequestDto);
    }

    @Override
    public Resource getObject(AwsS3RequestDto awsS3RequestDto) {
        return awsS3Handler.getObject(awsS3RequestDto);
    }

    @Override
    public void deleteObject(AwsS3RequestDto awsS3RequestDto) {
        awsS3Handler.deleteObject(awsS3RequestDto);
    }
}
