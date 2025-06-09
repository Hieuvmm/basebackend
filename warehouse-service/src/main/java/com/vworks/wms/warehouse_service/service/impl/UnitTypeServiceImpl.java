package com.vworks.wms.warehouse_service.service.impl;

import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.utils.StatusUtil;
import com.vworks.wms.warehouse_service.entities.UnitTypeEntity;
import com.vworks.wms.warehouse_service.models.request.unitType.*;
import com.vworks.wms.warehouse_service.models.response.unitType.*;
import com.vworks.wms.warehouse_service.repository.UnitTypeRepository;
import com.vworks.wms.warehouse_service.service.UnitTypeService;
import com.vworks.wms.warehouse_service.utils.Commons;
import com.vworks.wms.warehouse_service.utils.ExceptionTemplate;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnitTypeServiceImpl implements UnitTypeService {
    private final UnitTypeRepository unitTypeRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<PostListUnitTypeResponse> postListUnitType(PostListUnitTypeRequest requestBody) {
        log.info("{} postListUnitType requestBody {}", getClass().getSimpleName(), requestBody);
        Pageable pageable = PageRequest.of(requestBody.getPage() - 1, requestBody.getLimit(), Sort.by("createdDate").descending());

        Page<UnitTypeEntity> page = unitTypeRepository.findAll(unitTypeSpecification(requestBody), pageable);

        List<PostListUnitTypeResponse> list = page.getContent().stream().map(e ->
                modelMapper.map(e, PostListUnitTypeResponse.class)
        ).toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public PostCreateUnitTypeResponse postCreateUnitType(PostCreateUnitTypeRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postCreateUnitType requestBody {}", getClass().getSimpleName(), requestBody);

        Optional<UnitTypeEntity> optionalUnitTypeCode = unitTypeRepository.findByCodeOrName(requestBody.getCode(), null);

        if (optionalUnitTypeCode.isPresent()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.CODE_EXIST.getCode(), ExceptionTemplate.CODE_EXIST.getMessage());
        }
        Optional<UnitTypeEntity> optionalUnitTypeName = unitTypeRepository.findByCodeOrName(null, requestBody.getName());

        if (optionalUnitTypeName.isPresent()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }
        UnitTypeEntity unitTypeEntity = UnitTypeEntity.builder()
                .id(UUID.randomUUID().toString())
                .code(requestBody.getCode())
                .name(requestBody.getName())
                .description(requestBody.getDescription())
                .status(requestBody.getStatus())
                .createdBy(StringUtils.isBlank(httpServletRequest.getHeader(Commons.USER_CODE_FIELD)) ? httpServletRequest.getHeader(Commons.USER_CODE_FIELD) : null)
                .createdDate(new Timestamp(System.currentTimeMillis()))
                .build();
        unitTypeRepository.save(unitTypeEntity);
        return modelMapper.map(unitTypeEntity, PostCreateUnitTypeResponse.class);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public PostUpdateUnitTypeResponse postUpdateUnitType(PostUpdateUnitTypeRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        Optional<UnitTypeEntity> optionalUnitType = unitTypeRepository.findById(requestBody.getId());
        if (optionalUnitType.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        Optional<UnitTypeEntity> optionalUnitTypeName = unitTypeRepository.findByCodeOrName(null, requestBody.getName());

        if (optionalUnitTypeName.isPresent() && !StringUtils.equals(optionalUnitTypeName.get().getName(), optionalUnitType.get().getName())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }

        Optional<UnitTypeEntity> optionalUnitTypeCode = unitTypeRepository.findByCodeOrName(requestBody.getCode(), null);
        if (optionalUnitTypeCode.isPresent() && !StringUtils.equals(optionalUnitTypeCode.get().getCode(), optionalUnitType.get().getCode())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.CODE_EXIST.getCode(), ExceptionTemplate.CODE_EXIST.getMessage());
        }

        UnitTypeEntity unitTypeEntity = optionalUnitType.get();
        unitTypeEntity.setName(requestBody.getName());
        unitTypeEntity.setCode(requestBody.getCode());
        unitTypeEntity.setStatus(requestBody.getStatus());
        unitTypeEntity.setDescription(requestBody.getDescription());
        unitTypeEntity.setUpdatedBy(StringUtils.isBlank(httpServletRequest.getHeader(Commons.USER_CODE_FIELD)) ? httpServletRequest.getHeader(Commons.USER_CODE_FIELD) : null);
        unitTypeEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        unitTypeRepository.save(unitTypeEntity);
        return modelMapper.map(unitTypeEntity, PostUpdateUnitTypeResponse.class);
    }

    @Override
    public PostDetailUnitTypeResponse postDetailUnitType(PostDetailUnitTypeRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        Optional<UnitTypeEntity> optionalProvider = unitTypeRepository.findById(requestBody.getId());
        if (optionalProvider.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        return modelMapper.map(optionalProvider.get(), PostDetailUnitTypeResponse.class);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public PostDeleteUnitTypeResponse postDeleteUnitType(PostDeleteUnitTypeRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        Optional<UnitTypeEntity> optionalUnitType = unitTypeRepository.findById(requestBody.getId());
        if (optionalUnitType.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        if (StringUtils.equals(optionalUnitType.get().getStatus(), StatusUtil.ACTIVE.name())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.STATUS_INVALID.getCode(), ExceptionTemplate.STATUS_INVALID.getMessage());
        }
        UnitTypeEntity unitTypeEntity = optionalUnitType.get();
        unitTypeEntity.setStatus(StatusUtil.DELETED.name());
        unitTypeRepository.save(unitTypeEntity);
        return modelMapper.map(unitTypeEntity, PostDeleteUnitTypeResponse.class);
    }

    private Specification<UnitTypeEntity> unitTypeSpecification(PostListUnitTypeRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.notEqual(root.get("status"), StatusUtil.DELETED.name()));
            if (StringUtils.isNotBlank(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            String valueSearchText = "%" + request.getSearchText() + "%";
            if (StringUtils.isNotEmpty(request.getSearchText())) {
                Predicate code = criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), valueSearchText.toLowerCase());
                Predicate name = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), valueSearchText.toLowerCase());
                predicates.add(criteriaBuilder.or(code, name));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
