package com.vworks.wms.common_lib.security;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vworks.wms.common_lib.config.CommonLibConfigProperties;
import com.vworks.wms.common_lib.model.idm.IdmAppResource;
import com.vworks.wms.common_lib.service.CaffeineCacheService;
import com.vworks.wms.common_lib.service.IdmService;
import com.vworks.wms.common_lib.utils.Commons;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.keycloak.OAuth2Constants;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppAuthorizationFilter extends OncePerRequestFilter {
    private final Gson gson = new Gson();
    private final CaffeineCacheService cacheService;
    private final CommonLibConfigProperties commonConfigProperties;
    private final JwtDecoder jwtDecoder;
    private final IdmService idmService;
    @Value("${common.publicEndpoints}")
    private String[] publicEndpoints;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            boolean isPublic = Arrays.stream(publicEndpoints).anyMatch(uri -> request.getRequestURI().contains(uri));
            if (isPublic) {
                filterChain.doFilter(request, response);
                return;
            }

            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (StringUtils.isBlank(authHeader) || !StringUtils.startsWith(authHeader, "Bearer")) {
                log.error("{} The request is missing an authentication header!", getClass().getSimpleName());
                throw new AccessDeniedException("Missing or Invalid Authorization Header => Access Denied");
            }

            String accessToken = authHeader.replace("Bearer ", "");
            Jwt jwt = jwtDecoder.decode(accessToken);
            cacheAuthorization(accessToken, jwt.getClaimAsString(Commons.FIELD_JWT_SID));
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    private void cacheAuthorization(String accessToken, String cacheKey) {
        try {
            String authorizationCacheName = Commons.PREFIX_APP_AUTHORIZATION_CACHE_NAME + commonConfigProperties.getKeycloak().getRealm();
            if (Objects.isNull(cacheService.get(authorizationCacheName, cacheKey))) {
                Map<Object, Object> properties = new HashMap<>();
                properties.put(OAuth2Constants.GRANT_TYPE, OAuth2Constants.UMA_GRANT_TYPE);
                properties.put(Commons.FIELD_AUTHORIZATION, "Bearer " + accessToken);
                AccessTokenResponse accessTokenResponse = idmService.handleToFetchAccessToken(properties);

                if (Objects.isNull(accessTokenResponse)) {
                    return;
                }

                Jwt rptJwt = jwtDecoder.decode(accessTokenResponse.getToken());
                String cacheRealmName = Commons.PREFIX_KEYCLOAK_CACHE_NAME + commonConfigProperties.getKeycloak().getRealm();
                String cacheResourceKey = String.join("_", commonConfigProperties.getKeycloak().getClientId(), Commons.SUFFIX_ALL_RESOURCES_CACHE_KEY);
                Object clientResourcesCache = cacheService.get(cacheRealmName, cacheResourceKey);
                List<IdmAppResource> appResources = gson.fromJson(gson.toJson(clientResourcesCache), new TypeToken<List<IdmAppResource>>() {
                }.getType());

                Map<String, Object> authorizationMap = rptJwt.getClaimAsMap(Commons.FIELD_AUTHORIZATION);
                List<Map<String, Object>> permissions = gson.fromJson(gson.toJson(authorizationMap.get(Commons.FIELD_PERMISSIONS)), new TypeToken<List<Map<String, Object>>>() {
                }.getType());

                if (!CollectionUtils.isEmpty(appResources) && !CollectionUtils.isEmpty(permissions)) {
                    List<String> permissionUris = permissions.stream()
                            .map(per -> {
                                IdmAppResource resource = appResources.stream()
                                        .filter(rs -> StringUtils.equals(rs.getResourceId(), String.valueOf(per.get("rsid"))))
                                        .findFirst()
                                        .orElse(new IdmAppResource());
                                List<String> scopes = gson.fromJson(gson.toJson(per.get("scopes")), new TypeToken<List<String>>() {
                                }.getType());

                                if (CollectionUtils.isEmpty(scopes)) {
                                    return null;
                                }
                                return scopes.stream()
                                        .map(s -> {
                                            Map<String, List<String>> attribute = resource.getAttributes();
                                            return attribute.get(s);
                                        })
                                        .filter(Objects::nonNull)
                                        .flatMap(List::stream)
                                        .toList();
                            })
                            .filter(Objects::nonNull)
                            .flatMap(List::stream)
                            .filter(StringUtils::isNotBlank)
                            .toList();
                    cacheService.put(cacheKey, permissionUris, authorizationCacheName, accessTokenResponse.getExpiresIn(), TimeUnit.SECONDS);
                }
            }
        } catch (Exception e) {
            log.error("{} Exception {}", getClass().getSimpleName(), e);
        }

    }
}
