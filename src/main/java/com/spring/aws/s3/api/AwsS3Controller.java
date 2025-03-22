package com.spring.aws.s3.api;

import com.spring.aws.s3.domain.dto.AwsS3RequestDto;
import com.spring.aws.s3.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/aws-s3")
public class AwsS3Controller {

    private final AwsS3Service awsS3Service;

    @PutMapping
    public ResponseEntity<Void> putObject(AwsS3RequestDto awsS3RequestDto) {
        awsS3Service.putObject(awsS3RequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Resource> getObject(AwsS3RequestDto awsS3RequestDto) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + awsS3RequestDto.getFileName() + "\"")
                .body(awsS3Service.getObject(awsS3RequestDto));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteObject(AwsS3RequestDto awsS3RequestDto) {
        awsS3Service.deleteObject(awsS3RequestDto);
        return ResponseEntity.ok().build();
    }
}
