package com.vworks.wms.common_lib.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "common")
@Data
@Component
public class CommonLibConfigProperties {
    private KeycloakProperties keycloak;

    @Data
    public static class KeycloakProperties {
        private String serverUrl;
        private String realm;
        private String clientId;
        private String clientSecret;
        private String username;
        private String password;
        private Boolean enable;
    }
}
