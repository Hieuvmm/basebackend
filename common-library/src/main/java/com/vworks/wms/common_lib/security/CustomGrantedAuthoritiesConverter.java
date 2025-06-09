package com.vworks.wms.common_lib.security;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vworks.wms.common_lib.utils.Commons;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private final Gson gson = new Gson();

    @Override
    public Collection<GrantedAuthority> convert(@NotNull Jwt source) {
        Object userAuthority = extractUserAuthority(source);

        if (userAuthority instanceof List<?>) {
            List<?> userAuthorities = (List<?>) userAuthority;
            return userAuthorities.stream()
                    .filter(String.class::isInstance)
                    .map(auth -> new SimpleGrantedAuthority(String.valueOf(auth)))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return Collections.emptyList();
    }

//    @Override
//    public Collection<GrantedAuthority> convert(Jwt jwt) {
//        return ((Map<String, Collection<?>>) jwt.getClaims().getOrDefault(Commons.FIELD_REALM_ACCESS, Collections.emptyMap()))
//                .getOrDefault("roles", Collections.emptyList()).stream().map(Object::toString)
//                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
//
//    }

    private Object extractUserAuthority(Jwt source) {
        Map<String, Object> authorizationMap = source.getClaimAsMap(Commons.FIELD_AUTHORIZATION);
        Map<String, Object> resourceAccessMap = source.getClaimAsMap(Commons.FIELD_RESOURCE_ACCESS);
        Map<String, Object> realmAccessMap = source.getClaimAsMap(Commons.FIELD_REALM_ACCESS);
        Object clientId = Objects.nonNull(source.getClaim(Commons.FIELD_JWT_AZP)) ? source.getClaim(Commons.FIELD_JWT_AZP) : source.getClaim(Commons.FIELD_CLIENT_ID);

        if (Objects.nonNull(authorizationMap)) {
            return getPermissions(authorizationMap);
        }

        if (Objects.nonNull(resourceAccessMap) && Objects.nonNull(clientId) && resourceAccessMap.containsKey(String.valueOf(clientId))) {
            return getClientRoles(resourceAccessMap, clientId);
        }

        return Objects.nonNull(realmAccessMap) ? realmAccessMap.get(Commons.FIELD_ROLES) : null;
    }

    private Object getPermissions(Map<String, Object> authorizationMap) {
        String permissionJson = gson.toJson(authorizationMap.get(Commons.FIELD_PERMISSIONS));
        List<Map<String, Object>> permissions = gson.fromJson(permissionJson, new TypeToken<List<Map<String, Object>>>() {
        }.getType());
        String requestUrl = getCurrentRequestUrl();
        return permissions.stream()
                .filter(per -> StringUtils.contains(requestUrl, String.valueOf(per.get(Commons.FIELD_RESOURCE_NAME))))
                .map(per -> (List<?>) per.get(Commons.FIELD_SCOPES))
                .flatMap(scopes -> scopes.stream().filter(String.class::isInstance).map(String.class::cast))
                .toList();
    }

    private Object getClientRoles(Map<String, Object> resourceAccessMap, Object clientId) {
        String clientAccessJson = gson.toJson(resourceAccessMap.get(String.valueOf(clientId)));
        Map<String, Object> clientAccessMap = gson.fromJson(clientAccessJson, new TypeToken<Map<String, Object>>() {
        }.getType());
        return clientAccessMap != null ? clientAccessMap.get(Commons.FIELD_ROLES) : null;
    }

    private String getCurrentRequestUrl() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return request.getRequestURL().toString();
        }
        return null;
    }


}
