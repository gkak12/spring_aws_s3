package com.spring.aws.s3.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AwsS3RequestDto {

    private String bucketName;
    private String fileName;
    private MultipartFile multipartFile;
}
