package com.vworks.wms.admin_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "as-properties")
@Component
@Data
public class AdminServiceConfigProperties {
    private String apiPrefix;
}
