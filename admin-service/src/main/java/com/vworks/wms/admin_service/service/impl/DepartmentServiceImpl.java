package com.vworks.wms.admin_service.service.impl;

import com.google.gson.Gson;
import com.vworks.wms.admin_service.entity.DepartmentEntity;
import com.vworks.wms.admin_service.model.requestBody.PostCreateDepartmentRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostDeleteDepartmentRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostSearchDepartmentRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostUpdateDepartmentRequestBody;
import com.vworks.wms.admin_service.repository.DepartmentRepository;
import com.vworks.wms.admin_service.service.DepartmentService;
import com.vworks.wms.admin_service.utils.ASExceptionTemplate;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.service.ServiceUtils;
import com.vworks.wms.common_lib.utils.Commons;
import com.vworks.wms.common_lib.utils.StatusUtil;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final ServiceUtils serviceUtils;
    private final ModelMapper modelMapper;

    @Override
    public Object postCreateDepartment(PostCreateDepartmentRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postCreateDepartment requestBody {} ", getClass().getSimpleName(), requestBody);
        if (Boolean.TRUE.equals(departmentRepository.existsAllByCode(requestBody.getCode()))) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.DATA_EXISTED.getCode(), ASExceptionTemplate.DATA_EXISTED.getMessage());
        }

        DepartmentEntity departmentEntity = DepartmentEntity.builder()
                .id(UUID.randomUUID().toString())
                .createdDate(new Timestamp(System.currentTimeMillis()))
                .createdBy(httpServletRequest.getHeader(Commons.FIELD_USER_CODE))
                .build();

        modelMapper.map(requestBody, departmentEntity);

        log.info("{} postCreateDepartment departmentEntity {}", getClass().getSimpleName(), new Gson().toJson(departmentEntity));
        departmentRepository.save(departmentEntity);
        return StatusUtil.SUCCESS;
    }

    @Override
    public Object postUpdateDepartment(PostUpdateDepartmentRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postUpdateDepartment requestBody {} ", getClass().getSimpleName(), requestBody);
        DepartmentEntity departmentEntity = departmentRepository.findFirstByCodeOrId(requestBody.getCode(), null);

        if (Objects.isNull(departmentEntity)) {
            log.info("{} postUpdateDepartment departmentEntity is null", getClass().getSimpleName());
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.DATA_NOT_FOUND.getCode(), ASExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        modelMapper.map(requestBody, departmentEntity);

        departmentEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        departmentEntity.setUpdatedBy(httpServletRequest.getHeader(Commons.FIELD_USER_CODE));
        departmentRepository.save(departmentEntity);
        return StatusUtil.SUCCESS.name();
    }

    @Override
    public Page<DepartmentEntity> postSearchDepartment(PostSearchDepartmentRequestBody requestBody, HttpServletRequest httpServletRequest) {
        log.info("{} postSearchDepartment requestBody {} ", getClass().getSimpleName(), requestBody);
        Pageable pageable = serviceUtils.handlePageable(requestBody.getPage(), requestBody.getLimit(), requestBody.getOrders());

        Specification<DepartmentEntity> departmentSpecification = handleDepartmentEntitySpecification(requestBody);

        Page<DepartmentEntity> departmentEntityPage = departmentRepository.findAll(departmentSpecification, pageable);

        if (CollectionUtils.isEmpty(departmentEntityPage.getContent())) {
            return new PageImpl<>(new ArrayList<>(), pageable, departmentEntityPage.getTotalElements());
        }
        log.info("{} postSearchDepartment departmentEntityPage {} ", getClass().getSimpleName(), departmentEntityPage);
        return new PageImpl<>(departmentEntityPage.getContent(), pageable, departmentEntityPage.getTotalElements());
    }

    @Override
    public Object postDeleteDepartment(PostDeleteDepartmentRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postDeleteDepartment with requestBody {} ", getClass().getSimpleName(), requestBody);
        DepartmentEntity departmentEntity = departmentRepository.findFirstByCodeOrId(requestBody.getCode(), null);
        if (Objects.isNull(departmentEntity)) {
            log.error("{} postDeleteDepartment Job position data isn't exist! ", getClass().getSimpleName());
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.DATA_NOT_FOUND.getCode(), ASExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        departmentEntity.setStatus(StatusUtil.DELETED.name());
        departmentEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        departmentEntity.setUpdatedBy(httpServletRequest.getHeader(Commons.FIELD_USER_CODE));

        departmentRepository.save(departmentEntity);
        return StatusUtil.SUCCESS.name();
    }

    private Specification<DepartmentEntity> handleDepartmentEntitySpecification(PostSearchDepartmentRequestBody requestBody) {
        return (root, query, builder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotEmpty(requestBody.getStatus())) {
                Predicate statusPredicate = builder.equal(root.get(Commons.FIELD_STATUS), requestBody.getStatus());
                predicateList.add(statusPredicate);
            }

            if (StringUtils.isNotEmpty(requestBody.getSearchText())) {
                Predicate codePredicate = builder.equal(builder.lower(root.get(Commons.FIELD_CODE)), "%" + requestBody.getSearchText().toLowerCase() + "%");
                Predicate namePredicate = builder.like(builder.lower(root.get(Commons.FIELD_NAME)), "%" + requestBody.getSearchText().toLowerCase() + "%");
                predicateList.add(builder.or(codePredicate, namePredicate));
            }

            return builder.and(predicateList.toArray(new Predicate[0]));
        };
    }
}
