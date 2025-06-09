package com.vworks.wms.common_lib.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vworks.wms.common_lib.config.CommonLibConfigProperties;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.model.idm.IdmAppScope;
import com.vworks.wms.common_lib.model.idm.request.*;
import com.vworks.wms.common_lib.model.idm.IdmAppResource;
import com.vworks.wms.common_lib.model.idm.IdmAppRole;
import com.vworks.wms.common_lib.model.idm.IdmAppPermission;
import com.vworks.wms.common_lib.model.idm.response.IdmAppPermissionListResponse;
import com.vworks.wms.common_lib.model.idm.response.IdmAppResourceListResponse;
import com.vworks.wms.common_lib.model.idm.response.IdmAppRoleListResponse;
import com.vworks.wms.common_lib.model.request.IdmUpdateUserAttributeRequest;
import com.vworks.wms.common_lib.service.CaffeineCacheService;
import com.vworks.wms.common_lib.service.IdmService;
import com.vworks.wms.common_lib.utils.Commons;
import com.vworks.wms.common_lib.utils.MessageUtil;
import com.vworks.wms.common_lib.utils.StatusUtil;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.authorization.*;
import org.keycloak.representations.userprofile.config.UPConfig;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdmServiceImpl implements IdmService {
    private final CaffeineCacheService cacheService;
    private final RestTemplate restTemplate;
    private final Keycloak keycloakClientForAdmin;
    private final CommonLibConfigProperties commonLibConfigProperties;
    private final Gson gson = new Gson();

    @Override
    public Integer handleToCreateUser(IdmCreateUserRequest requestBody) {
        log.info("{} handleToCreateUser with username {}, realm {}", getClass().getSimpleName(), requestBody.getUsername(), commonLibConfigProperties.getKeycloak().getRealm());

        UsersResource userResource = keycloakClientForAdmin.realm(commonLibConfigProperties.getKeycloak().getRealm()).users();

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(Boolean.FALSE);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(requestBody.getPassword());

        UserRepresentation user = new UserRepresentation();
        user.setUsername(requestBody.getUsername());
        user.setLastName(requestBody.getFullName());
        user.setEmail(requestBody.getEmail());
        user.setEnabled(true);
        user.setCredentials(List.of(credential));

        Map<String, List<String>> userAttributes = new HashMap<>();
        userAttributes.put(Commons.FIELD_USER_CODE, List.of(requestBody.getUserCode()));
        userAttributes.put(Commons.FIELD_USER_ID, List.of(requestBody.getUserId()));

        user.setAttributes(userAttributes);
        Response response = userResource.create(user);
        log.info("{} handleToCreateUser with response {}", getClass().getSimpleName(), response.getStatusInfo().toString());
        return response.getStatus();
    }

    @Override
    public Integer handleToUpdateUser(IdmUpdateUserRequest requestBody) {
        log.info("{} handleToUpdateUser with username {}, realm {}", getClass().getSimpleName(), requestBody.getUsername(), commonLibConfigProperties.getKeycloak().getRealm());

        UsersResource usersResource = keycloakClientForAdmin.realm(commonLibConfigProperties.getKeycloak().getRealm()).users();
        Optional<UserRepresentation> existingUser = usersResource.search(requestBody.getUsername()).stream().findFirst();
        if (existingUser.isEmpty()) {
            return 404;
        }

        UserResource userResource = usersResource.get(existingUser.get().getId());
        UserRepresentation userRepresentation = userResource.toRepresentation();
        boolean isUpdate = false;
        if (StringUtils.isNotBlank(requestBody.getStatus())) {
            Boolean isEnable = !StringUtils.equals(requestBody.getStatus(), StatusUtil.ACTIVE.name()) ? Boolean.FALSE : userRepresentation.isEnabled();
            userRepresentation.setEnabled(isEnable);
            isUpdate = true;
        }

        if (StringUtils.isNotBlank(requestBody.getPassword())) {
            if (Objects.nonNull(userRepresentation.getCredentials())) {
                userRepresentation.getCredentials().clear();
            } else {
                userRepresentation.setCredentials(new ArrayList<>());
            }

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(requestBody.getPassword());
            credential.setTemporary(false);

            userRepresentation.getCredentials().add(credential);
            isUpdate = true;
        }

        if (!isUpdate) {
            return 400;
        }
        userResource.update(userRepresentation);
        return 200;
    }

    @Override
    public AccessTokenResponse handleToFetchAccessToken(Map<Object, Object> properties) {
        try {
            log.info("{} handleToFetchAccessToken with realm {} with type {}", getClass().getSimpleName(), commonLibConfigProperties.getKeycloak().getRealm(), properties.get(OAuth2Constants.GRANT_TYPE));
            if (Objects.isNull(properties.get(Commons.FIELD_GRANT_TYPE))) {
                throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name(), String.format(MessageUtil.TEMPLATE_REQUEST_INVALID, Commons.FIELD_GRANT_TYPE));
            }

            String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token",
                    commonLibConfigProperties.getKeycloak().getServerUrl(), commonLibConfigProperties.getKeycloak().getRealm());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add(OAuth2Constants.CLIENT_ID, commonLibConfigProperties.getKeycloak().getClientId());
            body.add(OAuth2Constants.CLIENT_SECRET, commonLibConfigProperties.getKeycloak().getClientSecret());

            if (Objects.equals(OAuth2Constants.PASSWORD, properties.get(OAuth2Constants.GRANT_TYPE))) {
                body.add(OAuth2Constants.GRANT_TYPE, OAuth2Constants.PASSWORD);
                body.add(OAuth2Constants.USERNAME, String.valueOf(properties.get(Commons.FIELD_USER_NAME)));
                body.add(OAuth2Constants.PASSWORD, String.valueOf(properties.get(Commons.FIELD_PASSWORD)));
            }

            if (Objects.equals(OAuth2Constants.REFRESH_TOKEN, properties.get(OAuth2Constants.GRANT_TYPE))) {
                body.add(OAuth2Constants.GRANT_TYPE, OAuth2Constants.REFRESH_TOKEN);
                body.add(OAuth2Constants.REFRESH_TOKEN, String.valueOf(properties.get(Commons.FIELD_REFRESH_TOKEN)));
            }

            if (Objects.equals(OAuth2Constants.UMA_GRANT_TYPE, properties.get(OAuth2Constants.GRANT_TYPE))) {
                body.add(OAuth2Constants.GRANT_TYPE, OAuth2Constants.UMA_GRANT_TYPE);
                body.add(OAuth2Constants.AUDIENCE, Objects.nonNull(properties.get(OAuth2Constants.AUDIENCE)) ? String.valueOf(properties.get(OAuth2Constants.AUDIENCE)) : commonLibConfigProperties.getKeycloak().getClientId());
                headers.set(HttpHeaders.AUTHORIZATION, String.valueOf(properties.get(Commons.FIELD_AUTHORIZATION)));
            }

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<AccessTokenResponse> response = restTemplate.postForEntity(
                    tokenUrl,
                    requestEntity,
                    AccessTokenResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("{} handleToFetchAccessToken exception: {}", getClass().getSimpleName(), e);
            return null;
        }
    }

    @Override
    public Object handleToLogOut(Map<Object, Object> properties) {
        log.info("{} handleToLogOut with realm {}, properties {}", getClass().getSimpleName(), commonLibConfigProperties.getKeycloak().getRealm(), properties.size());
        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/logout",
                commonLibConfigProperties.getKeycloak().getServerUrl(), commonLibConfigProperties.getKeycloak().getRealm());
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(OAuth2Constants.CLIENT_ID, commonLibConfigProperties.getKeycloak().getClientId());
        body.add(OAuth2Constants.CLIENT_SECRET, commonLibConfigProperties.getKeycloak().getClientSecret());
        body.add(OAuth2Constants.REFRESH_TOKEN, String.valueOf(properties.get(Commons.FIELD_REFRESH_TOKEN)));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Object> response = restTemplate.postForEntity(
                tokenUrl,
                requestEntity,
                Object.class
        );
        return response.getBody();
    }

    @Override
    public Object updateUserAttributes(IdmUpdateUserAttributeRequest request) {
        log.info("{} updateUserAttributes with request {}", getClass().getSimpleName(), request);
        UserProfileResource userProfileResource = keycloakClientForAdmin.realm(commonLibConfigProperties.getKeycloak().getRealm()).users().userProfile();
        UPConfig upConfig = userProfileResource.getConfiguration();
        request.getAttributes().forEach(upConfig::addOrReplaceAttribute);
        log.info("{} updateUserAttributes with upConfig {}", getClass().getSimpleName(), upConfig);
        userProfileResource.update(upConfig);
        return true;
    }

    @Override
    public IdmAppResourceListResponse getAllAppResources(String realm, String clientId) {
        String cacheName = Commons.PREFIX_KEYCLOAK_CACHE_NAME + realm;
        String cacheResourceKey = String.join("_", clientId, Commons.SUFFIX_ALL_RESOURCES_CACHE_KEY);
        Object clientResourcesCache = cacheService.get(cacheName, cacheResourceKey);
        List<IdmAppResource> cacheResources = gson.fromJson(gson.toJson(clientResourcesCache), new TypeToken<List<IdmAppResource>>() {
        }.getType());
        if (CollectionUtils.isEmpty(cacheResources)) {
            ClientsResource clientsResource = keycloakClientForAdmin.realm(realm).clients();
            String clientUuid = clientsResource.findByClientId(clientId).get(0).getId();
            List<ResourceRepresentation> appResources = clientsResource.get(clientUuid).authorization().resources().resources();
            cacheResources = appResources.stream()
                    .map(resource -> IdmAppResource.builder()
                            .resourceId(resource.getId())
                            .name(resource.getName())
                            .uris(resource.getUris())
                            .type(resource.getType())
                            .displayName(resource.getDisplayName())
                            .attributes(resource.getAttributes())
                            .scopes(resource.getScopes().stream()
                                    .map(scope -> IdmAppScope.builder().scopeId(scope.getId()).name(scope.getName()).build())
                                    .collect(Collectors.toSet()))
                            .build())
                    .toList();
            cacheService.put(cacheResourceKey, cacheResources, cacheName);
        }

        return IdmAppResourceListResponse.builder().data(cacheResources).build();
    }

    @Override
    public IdmAppRoleListResponse getAllAppRoles(String realm, String clientId) {
        String cacheName = Commons.PREFIX_KEYCLOAK_CACHE_NAME + realm;
        String cacheRoleListKey = String.join("_", clientId, Commons.SUFFIX_ALL_ROLES_CACHE_KEY);
        Object rolesCache = cacheService.get(cacheName, cacheRoleListKey);
        if (Objects.nonNull(rolesCache)) {
            List<IdmAppRole> roles = gson.fromJson(gson.toJson(rolesCache),  new TypeToken<List<IdmAppRole>>() {
            }.getType());
            return  IdmAppRoleListResponse.builder().data(roles).build();
        }

        ClientsResource clientsResource = keycloakClientForAdmin.realm(realm).clients();
        String clientUuid = clientsResource.findByClientId(clientId).get(0).getId();
        List<RoleRepresentation> roleRepresentations = clientsResource.get(clientUuid).roles().list();
        return IdmAppRoleListResponse.builder().data(mapRoleInfo(roleRepresentations)).build();
    }

    private List<IdmAppRole> mapRoleInfo(List<RoleRepresentation> roleRepresentations) {
        return roleRepresentations.stream()
                .filter(e -> !StringUtils.equals(e.getName(), "uma_protection"))
                .map(role -> IdmAppRole.builder()
                        .roleId(role.getId())
                        .name(role.getName())
                        .description(role.getDescription())
                        .build()
                ).toList();
    }

    @Override
    public IdmAppRoleListResponse searchAppRoles(IdmAppRoleListRequest request) {
        try {
            log.info("{} searchAppRoles for request = {}", getClass().getSimpleName(), request);

            ClientsResource clientsResource = keycloakClientForAdmin.realm(request.getRealm()).clients();

            String clientUuid = clientsResource.findByClientId(request.getClientId()).get(0).getId();

            int first = Objects.nonNull(request.getFirst()) ? request.getFirst() : 0;
            int max = Objects.nonNull(request.getMax()) ? request.getMax() : Integer.MAX_VALUE;

            List<RoleRepresentation> roleRepresentations = clientsResource.get(clientUuid).roles().list(request.getKey(),first, max);
            return IdmAppRoleListResponse.builder().data(mapRoleInfo(roleRepresentations)).build();
        } catch (Exception e) {
            log.error("{} searchAppRoles exception = {}", getClass().getSimpleName(), e);
            return IdmAppRoleListResponse.builder().build();
        }
    }

    @Override
    public IdmAppRole getDetailRole(String realm, String clientId, String roleName) {
        try {
            log.info("{} getDetailRole for role = {}", getClass().getSimpleName(), roleName);
            ClientsResource clientsResource = keycloakClientForAdmin.realm(realm).clients();

            String clientUuid = clientsResource.findByClientId(clientId).get(0).getId();
            RoleRepresentation role = clientsResource.get(clientUuid).roles().get(roleName).toRepresentation();

            return IdmAppRole.builder()
                    .roleId(role.getId())
                    .name(role.getName())
                    .description(role.getDescription())
                    .build();
        } catch (Exception e) {
            log.error("{} getClientRoles exception = {}", getClass().getSimpleName(), e);
            return null;
        }
    }

    @Override
    public IdmAppPermissionListResponse getAllAppPermissions(String realm, String clientId) {
        String cacheName = Commons.PREFIX_KEYCLOAK_CACHE_NAME + realm;
        String cacheAllPermissionsKey = String.join("_", clientId, Commons.SUFFIX_ALL_PERMISSIONS_CACHE_KEY);
        Object cacheAllPermissions = cacheService.get(cacheName, cacheAllPermissionsKey);
        List<IdmAppPermission> allPermission = new ArrayList<>();
        if (Objects.nonNull(cacheAllPermissions)) {
            allPermission = gson.fromJson(gson.toJson(cacheAllPermissions), new TypeToken<List<IdmAppPermission>>() {
            }.getType());
        } else {
            ClientsResource clientsResource = keycloakClientForAdmin.realm(realm).clients();
            String clientUuid = clientsResource.findByClientId(clientId).get(0).getId();
            List<PolicyRepresentation> allPolicies = clientsResource.get(clientUuid).authorization().policies().policies();

            if (!CollectionUtils.isEmpty(allPolicies)) {
                List<PolicyRepresentation> policyRepresentations = allPolicies.stream()
                        .filter(e -> StringUtils.equals(e.getType(), new ResourcePermissionRepresentation().getType())
                                || StringUtils.equals(e.getType(), new ScopePermissionRepresentation().getType()) )
                        .toList();

                allPermission = Objects.requireNonNull(policyRepresentations).stream()
                        .map(per -> IdmAppPermission.builder()
                                .permissionId(per.getId())
                                .name(per.getName())
                                .type(per.getType())
                                .description(per.getDescription())
                                .data(per.getResourcesData())
                                .build())
                        .toList();

                cacheService.put(cacheAllPermissionsKey, allPermission, cacheName);
            }
        }

        return IdmAppPermissionListResponse.builder().data(allPermission).build();
    }

    @Override
    public IdmAppPermissionListResponse getPermissionForRole(IdmPermissionForRoleRequest request) {
        log.info("{} getPermissionForRole for request = {}", getClass().getSimpleName(), request);
        String cacheName = Commons.PREFIX_KEYCLOAK_CACHE_NAME + commonLibConfigProperties.getKeycloak().getRealm();
        String cacheRolePermissionsKey = String.join("_", request.getClientId(), Commons.SUFFIX_ROLE_PERMISSIONS_CACHE_KEY, request.getRoleId());
        Object cacheObject = cacheService.get(cacheName, cacheRolePermissionsKey);
        List<IdmAppPermission> response;

        if (Objects.nonNull(cacheObject)) {
            response =  gson.fromJson(gson.toJson(cacheObject), new TypeToken<List<IdmAppPermission>>() {
            }.getType());

        } else {
            ClientsResource clientsResource = keycloakClientForAdmin.realm(request.getRealm()).clients();
            String clientUuid = clientsResource.findByClientId(request.getClientId()).get(0).getId();

            List<PolicyRepresentation> policyForRoles = getAllAppPolicies(request.getRealm(), request.getClientId());
            if (CollectionUtils.isEmpty(policyForRoles)) {
                return IdmAppPermissionListResponse.builder().build();
            }

            PolicyRepresentation policyForRole = policyForRoles.stream()
                    .filter(policy -> hasRoleInPolicy(policy, request.getRoleId()))
                    .findFirst()
                    .orElse(null);

            if (Objects.isNull(policyForRole)) {
                return IdmAppPermissionListResponse.builder().build();
            }

            List<PolicyRepresentation> resList = clientsResource.get(clientUuid).authorization().policies().policy(policyForRole.getId()).dependentPolicies();

            response = Objects.requireNonNull(resList).stream()
                    .map(per -> IdmAppPermission.builder()
                            .permissionId(per.getId())
                            .name(per.getName())
                            .type(per.getType())
                            .description(per.getDescription())
                            .data(per.getResourcesData())
                            .build())
                    .toList();

            cacheService.put(cacheRolePermissionsKey, response, cacheName);
        }
        log.info("{} getPermissionForRole for response = {}", getClass().getSimpleName(), response);
        return IdmAppPermissionListResponse.builder().data(response).build();
    }

    private boolean hasRoleInPolicy(PolicyRepresentation policy, String roleId) {
        if (Objects.nonNull(policy.getConfig()) && StringUtils.isNotBlank(policy.getConfig().get("roles"))) {
            List<RolePolicyRepresentation.RoleDefinition> roleDefinitions = gson.fromJson(policy.getConfig().get("roles"), new TypeToken<List<RolePolicyRepresentation.RoleDefinition>>() {
            }.getType());
            return roleDefinitions.stream().anyMatch(role -> StringUtils.equals(role.getId(), roleId));
        }
        return false;
    }

    private List<PolicyRepresentation> getAllAppPolicies(String realm ,String clientId) {
        String cacheName = Commons.PREFIX_KEYCLOAK_CACHE_NAME + realm;
        String cachePolicyForRoleKey = String.join("_", clientId, Commons.SUFFIX_ALL_POLICIES_CACHE_KEY);
        Object cacheObject = cacheService.get(cacheName, cachePolicyForRoleKey);

        if (Objects.nonNull(cacheObject)) {
            return gson.fromJson(gson.toJson(cacheObject), new TypeToken<List<PolicyRepresentation>>() {
            }.getType());
        }

        ClientsResource clientsResource = keycloakClientForAdmin.realm(realm).clients();
        String clientUuid = clientsResource.findByClientId(clientId).get(0).getId();
        List<PolicyRepresentation> policies = clientsResource.get(clientUuid).authorization().policies().policies();

        if (!CollectionUtils.isEmpty(policies)) {
            policies = policies.stream().filter(e -> StringUtils.equals(e.getType(), new RolePolicyRepresentation().getType())).toList();
            cacheService.put(cachePolicyForRoleKey, policies, cacheName);
        }

        return policies;
    }
}
