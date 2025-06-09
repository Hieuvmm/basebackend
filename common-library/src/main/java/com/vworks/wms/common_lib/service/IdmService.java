package com.vworks.wms.common_lib.service;

import com.vworks.wms.common_lib.model.idm.request.*;
import com.vworks.wms.common_lib.model.idm.IdmAppRole;
import com.vworks.wms.common_lib.model.idm.response.IdmAppPermissionListResponse;
import com.vworks.wms.common_lib.model.idm.response.IdmAppResourceListResponse;
import com.vworks.wms.common_lib.model.idm.response.IdmAppRoleListResponse;
import com.vworks.wms.common_lib.model.request.IdmUpdateUserAttributeRequest;
import org.keycloak.representations.AccessTokenResponse;

import java.util.Map;

public interface IdmService {
    Integer handleToCreateUser(IdmCreateUserRequest requestBody);
    Integer handleToUpdateUser(IdmUpdateUserRequest requestBody);
    AccessTokenResponse handleToFetchAccessToken(Map<Object, Object> additionProperties);
    Object handleToLogOut(Map<Object, Object> additionProperties);
    Object updateUserAttributes(IdmUpdateUserAttributeRequest request);
    IdmAppResourceListResponse getAllAppResources(String realm, String clientId);
    IdmAppRoleListResponse getAllAppRoles(String realm, String clientId);
    IdmAppRoleListResponse searchAppRoles(IdmAppRoleListRequest request);
    IdmAppRole getDetailRole(String realm, String clientId, String roleName);
    IdmAppPermissionListResponse getAllAppPermissions(String realm, String clientId);
    IdmAppPermissionListResponse getPermissionForRole(IdmPermissionForRoleRequest request);
}
