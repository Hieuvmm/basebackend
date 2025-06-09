package com.vworks.wms.common_lib.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vworks.wms.common_lib.config.CommonLibConfigProperties;
import com.vworks.wms.common_lib.service.AppAuthorizer;
import com.vworks.wms.common_lib.service.CaffeineCacheService;
import com.vworks.wms.common_lib.utils.Commons;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service("appAuthorizer")
public class AppAuthorizerImpl implements AppAuthorizer {
    private final CommonLibConfigProperties commonConfigProperties;
    private final CaffeineCacheService cacheService;
    private final Gson gson = new Gson();
    private final Environment env;

    @Override
    public boolean authorize(Authentication authentication, String action, Object callerObj) {
        log.info("{} authentication {}, callerObj {}", getClass().getSimpleName(), authentication, callerObj.getClass().getSimpleName());

        String securedPath = getCurrentRequestPath();
        log.info("{} securedPath {}", getClass().getSimpleName(), securedPath);
        if (StringUtils.isBlank(securedPath)) {
            return false;
        }

        if (isDevProfile()) return true;

        if (Boolean.FALSE.equals(commonConfigProperties.getKeycloak().getEnable())
                || StringUtils.isBlank(action)) {
            return true;
        }

        if (hasAuthority(authentication, Commons.ROLE_REALM_ADMIN)
                || hasAuthority(authentication, Commons.ROLE_CLIENT_ADMIN)
                || hasAuthority(authentication, Commons.ROLE_REALM_ADMINISTRATOR)) {
            return true;
        }

        boolean isAllow = false;
        List<String> perUris = new ArrayList<>();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String cacheKey = jwt.getClaimAsString(Commons.FIELD_JWT_SID);
            String authorizationCacheName = Commons.PREFIX_APP_AUTHORIZATION_CACHE_NAME + commonConfigProperties.getKeycloak().getRealm();

            Object rptToken = cacheService.get(authorizationCacheName, cacheKey);
            perUris = gson.fromJson(gson.toJson(rptToken), new TypeToken<List<String>>() {
            }.getType());
            isAllow = perUris.stream().anyMatch(e -> StringUtils.equals(e, securedPath));
        }

        if (!isAllow && !CollectionUtils.isEmpty(perUris) && callerObj instanceof RestController) {
            RequestMapping requestMapping = callerObj.getClass().getAnnotation(RequestMapping.class);
            if (requestMapping != null) {
                String finalSecuredPath = String.join("/", requestMapping.value()[0], action);
                isAllow = perUris.stream().anyMatch(e -> StringUtils.equalsIgnoreCase(e, finalSecuredPath));
            }
        }

        return isAllow;
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority::equalsIgnoreCase);
    }

    private String getCurrentRequestPath() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return request.getRequestURI();
        }
        return null;
    }

    public boolean isDevProfile() {
        return Arrays.asList(env.getActiveProfiles()).contains("dev");
    }
}
