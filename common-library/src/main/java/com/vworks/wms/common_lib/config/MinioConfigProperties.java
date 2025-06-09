package com.vworks.wms.common_lib.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "common.minio")
@Data
@Component
public class MinioConfigProperties {
    private String url;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String materialImageFolderStorage;
    private String projectAttachmentsFolderStorage;
}
