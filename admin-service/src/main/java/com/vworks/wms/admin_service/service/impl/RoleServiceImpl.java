package com.vworks.wms.admin_service.service.impl;

import com.vworks.wms.admin_service.model.requestBody.PostSearchRoleRequestBody;
import com.vworks.wms.admin_service.repository.RoleRepository;
import com.vworks.wms.admin_service.service.RoleService;
import com.vworks.wms.common_lib.config.CommonLibConfigProperties;
import com.vworks.wms.common_lib.model.idm.request.IdmAppRoleListRequest;
import com.vworks.wms.common_lib.model.idm.IdmAppRole;
import com.vworks.wms.common_lib.model.idm.response.IdmAppRoleListResponse;
import com.vworks.wms.common_lib.service.IdmService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final IdmService idmService;
    private final CommonLibConfigProperties commonConfigProperties;
    private final ModelMapper modelMapper;

//    @Override
//    public Object postCreateRole(PostCreateRoleRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
//        log.info("{} postCreateRole requestBody {}", getClass().getSimpleName(), requestBody);
//        if (Boolean.TRUE.equals(roleRepository.existsAllByCode(requestBody.getCode()))) {
//            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.DATA_EXISTED.getCode(), ASExceptionTemplate.DATA_EXISTED.getMessage());
//        }
//
//        RoleEntity roleEntity = RoleEntity.builder()
//                .id(UUID.randomUUID().toString())
//                .createdDate(new Timestamp(System.currentTimeMillis()))
//                .createdBy(httpServletRequest.getHeader(Commons.FIELD_USER_CODE))
//                .build();
//
//        modelMapper.map(requestBody, roleEntity);
//
//        log.info("{} postCreateRole roleEntity {}", getClass().getSimpleName(), new Gson().toJson(roleEntity));
//        roleRepository.save(roleEntity);
//        return StatusUtil.SUCCESS.name();
//    }
//
//    @Override
//    public Object postUpdateRole(PostUpdateRoleRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
//        log.info("{} postUpdateRole requestBody {}", getClass().getSimpleName(), requestBody);
//        RoleEntity roleEntity = roleRepository.findFirstByIdOrCode(requestBody.getId(), requestBody.getCode());
//        if (Objects.isNull(roleEntity)) {
//            log.info("{} postUpdateRole roleEntity is null", getClass().getSimpleName());
//            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.DATA_NOT_FOUND.getCode(), ASExceptionTemplate.DATA_NOT_FOUND.getMessage());
//        }
//
//        modelMapper.map(requestBody, roleEntity);
//
//        roleEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
//        roleEntity.setUpdatedBy(httpServletRequest.getHeader(Commons.FIELD_USER_CODE));
//        roleRepository.save(roleEntity);
//        return StatusUtil.SUCCESS;
//    }

    @Override
    public Page<IdmAppRole> postSearchRole(PostSearchRoleRequestBody requestBody, HttpServletRequest httpServletRequest) {
        log.info("{} postSearchRole requestBody {}", getClass().getSimpleName(), requestBody);
        int limitPage = Objects.nonNull(requestBody.getLimit()) ? requestBody.getLimit() : Integer.MAX_VALUE;
        int numPage = Objects.nonNull(requestBody.getPage()) ? requestBody.getPage() : 1;

        Pageable pageable = PageRequest.of(numPage - 1, limitPage);
        int first = (numPage - 1) * limitPage;

        IdmAppRoleListRequest searchAppRolesRequest = new IdmAppRoleListRequest();
        searchAppRolesRequest.setRealm(commonConfigProperties.getKeycloak().getRealm());
        searchAppRolesRequest.setClientId(commonConfigProperties.getKeycloak().getClientId());
        searchAppRolesRequest.setKey(requestBody.getSearchText());
        searchAppRolesRequest.setFirst(first);
        searchAppRolesRequest.setMax(limitPage);

        IdmAppRoleListResponse clientRoles = idmService.searchAppRoles(searchAppRolesRequest);
        log.info("{} postSearchRole clientRoles {}", getClass().getSimpleName(), clientRoles.getData());
        return new PageImpl<>(clientRoles.getData(), pageable, clientRoles.getData().size());
    }

    @Override
    public IdmAppRole postDetailRole(String roleName, HttpServletRequest httpServletRequest) {
        return idmService.getDetailRole(commonConfigProperties.getKeycloak().getRealm(), commonConfigProperties.getKeycloak().getClientId(), roleName);
    }
}
