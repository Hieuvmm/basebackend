package com.vworks.wms.common_lib.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
public class KeycloakConfiguration {
    @Value("${common.keycloak.serverUrl}")
    private String serverUrl;

    @Value("${common.keycloak.realm}")
    private String realm;

    @Value("${common.keycloak.clientId}")
    private String clientId;

    @Value("${common.keycloak.clientSecret}")
    private String clientSecret;

    @Bean
    public Keycloak keycloakClientForAdmin() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }
}
