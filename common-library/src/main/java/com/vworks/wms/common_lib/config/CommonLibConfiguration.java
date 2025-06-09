package com.vworks.wms.common_lib.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class CommonLibConfiguration {
    private final MinioConfigProperties minioConfigProperties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioConfigProperties.getUrl())
                .credentials(minioConfigProperties.getAccessKey(), minioConfigProperties.getSecretKey())
                .build();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
