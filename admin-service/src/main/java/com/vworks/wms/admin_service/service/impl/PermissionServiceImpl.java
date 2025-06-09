package com.vworks.wms.admin_service.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vworks.wms.admin_service.model.requestBody.PostDetailPermissionByRoleRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostSearchPermissionRequestBody;
import com.vworks.wms.admin_service.service.PermissionService;
import com.vworks.wms.admin_service.utils.ASExceptionTemplate;
import com.vworks.wms.common_lib.config.CommonLibConfigProperties;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.model.idm.request.IdmPermissionForRoleRequest;
import com.vworks.wms.common_lib.model.idm.IdmAppRole;
import com.vworks.wms.common_lib.model.idm.IdmAppPermission;
import com.vworks.wms.common_lib.model.idm.response.IdmAppPermissionListResponse;
import com.vworks.wms.common_lib.model.idm.response.IdmAppRoleListResponse;
import com.vworks.wms.common_lib.service.CaffeineCacheService;
import com.vworks.wms.common_lib.service.IdmService;
import com.vworks.wms.common_lib.utils.Commons;
import com.vworks.wms.common_lib.utils.IdmHandleUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final IdmService idmService;
    private final CaffeineCacheService cacheService;
    private final CommonLibConfigProperties commonConfigProperties;
    private final Gson gson = new Gson();

    @Override
    public List<IdmAppPermission> postDetailPermissionByRole(PostDetailPermissionByRoleRequestBody requestBody, HttpServletRequest httpServletRequest) {
        log.info("{} postDetailPermissionByRole requestBody {}", getClass().getSimpleName(), requestBody);
        String cacheName = Commons.PREFIX_KEYCLOAK_CACHE_NAME + commonConfigProperties.getKeycloak().getRealm();
        String cacheRoleListKey = String.join("_", commonConfigProperties.getKeycloak().getClientId(), Commons.SUFFIX_ALL_ROLES_CACHE_KEY);
        Object rolesCache = cacheService.get(cacheName, cacheRoleListKey);
        Map<String, IdmAppRole> mapRoles = new HashMap<>();
        if (Objects.nonNull(rolesCache)) {
            mapRoles = gson.fromJson(gson.toJson(rolesCache), new TypeToken<Map<String, IdmAppRole>>() {
            }.getType());
        } else {
            IdmAppRoleListResponse clientRoles = idmService.getAllAppRoles(commonConfigProperties.getKeycloak().getRealm(), commonConfigProperties.getKeycloak().getClientId());

            if (Objects.nonNull(clientRoles) && !CollectionUtils.isEmpty(clientRoles.getData())) {
                for (IdmAppRole role : clientRoles.getData()) {
                    mapRoles.put(role.getName(), role);
                }
                cacheService.put(cacheRoleListKey, mapRoles, cacheName);
            }
        }

        IdmAppRole role = mapRoles.get(requestBody.getRole());

        if (Objects.isNull(role)) {
            return new ArrayList<>();
        }

        IdmAppPermissionListResponse permissions = idmService.getPermissionForRole(IdmPermissionForRoleRequest.builder()
                .realm(commonConfigProperties.getKeycloak().getRealm())
                .clientId(commonConfigProperties.getKeycloak().getClientId())
                .roleId(role.getRoleId())
                .build());
        log.info("{} postDetailPermissionByRole permissions {}", getClass().getSimpleName(), permissions.getData());
        List<IdmAppPermission> permissionsByRole = permissions.getData();
        if (permissionsByRole.stream().anyMatch(per -> StringUtils.equalsIgnoreCase(per.getName(), "permission:all") && StringUtils.equalsIgnoreCase(per.getType(), "resource"))) {
            permissions = idmService.getAllAppPermissions(commonConfigProperties.getKeycloak().getRealm(), commonConfigProperties.getKeycloak().getClientId());
        }
        return permissions.getData();
    }

    @Override
    public Page<IdmAppPermission> postSearchPermission(PostSearchPermissionRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postSearchPermission requestBody {}", getClass().getSimpleName(), requestBody);
        if (Objects.isNull(requestBody)) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.REQUEST_INVALID.getCode(), ASExceptionTemplate.REQUEST_INVALID.getMessage());
        }

        IdmAppPermissionListResponse idmData = idmService.getAllAppPermissions(commonConfigProperties.getKeycloak().getRealm(), commonConfigProperties.getKeycloak().getClientId());
        int numPage = Objects.nonNull(requestBody.getPage()) ? requestBody.getPage() : 1;
        int limitPage = Objects.nonNull(requestBody.getLimit()) ? requestBody.getLimit() : Integer.MAX_VALUE;

        int first = (numPage - 1) * limitPage;

        Pageable pageable = PageRequest.of(numPage - 1, limitPage);
        if (Objects.isNull(idmData) || CollectionUtils.isEmpty(idmData.getData())) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        List<IdmAppPermission> data = idmData.getData().stream().sorted(IdmHandleUtil.buildAppPermissionComparatorSort(requestBody.getOrders())).toList();
        data = data.subList(first, Math.min(first + limitPage, data.size()));
        return new PageImpl<>(data, pageable, data.size());
    }
}
