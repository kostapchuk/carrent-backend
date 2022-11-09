package com.ostapchuk.car.rent.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AwsS3Config {

//    @Value("${aws.access.key.id}")
//    private final String accessKeyId;
//
//    @Value("${aws.access.key.secret}")
//    private final String accessKeySecret;
//
//    @Value("${aws.s3.region.name}")
//    private final String s3RegionName;
//
//    @Bean
//    public AmazonS3Client amazonS3Client() {
//        final BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKeyId, accessKeySecret);
//        return (AmazonS3Client) AmazonS3ClientBuilder
//                .standard()
//                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
//                .withRegion(s3RegionName)
//                .build();
//    }
}
