package com.vworks.wms.warehouse_service.service.impl;

import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.service.ServiceUtils;
import com.vworks.wms.common_lib.utils.StatusUtil;
import com.vworks.wms.warehouse_service.entities.ParameterEntity;
import com.vworks.wms.warehouse_service.entities.ParameterTypeEntity;
import com.vworks.wms.warehouse_service.models.request.parameter.PostCreateOrUpdateParameterReqBody;
import com.vworks.wms.warehouse_service.models.request.parameter.PostHandleByCodeParameterReqBody;
import com.vworks.wms.warehouse_service.models.request.parameter.PostListParameterReqBody;
import com.vworks.wms.warehouse_service.models.response.parameter.PostGetParameterResBody;
import com.vworks.wms.warehouse_service.repository.ParameterRepository;
import com.vworks.wms.warehouse_service.repository.ParameterTypeRepository;
import com.vworks.wms.warehouse_service.service.ParameterService;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ParameterServiceImpl implements ParameterService {
    private final ServiceUtils serviceUtils;
    private final ParameterRepository parameterRepo;
    private final ParameterTypeRepository parameterTypeRepo;
    private final ModelMapper modelMapper;

    @Override
    public Page<PostGetParameterResBody> postListParameter(PostListParameterReqBody reqBody) {
        log.info("{} postListParameter reqBody{}", getClass().getSimpleName(), reqBody);
        Pageable pageable = PageRequest.of(reqBody.getPage() - 1, reqBody.getLimit(), Sort.by("createdDate").descending());

        Page<ParameterEntity> page = parameterRepo.findAll(parameterEntitySpec(reqBody), pageable);

        List<PostGetParameterResBody> list = page.getContent().stream().map(e ->
                modelMapper.map(e, PostGetParameterResBody.class)
        ).toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Override
    public Object postCreateParameter(PostCreateOrUpdateParameterReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postCreateParameter reqBody{}", getClass().getSimpleName(), reqBody);

        Optional<ParameterEntity> optionalName = parameterRepo.findByCodeOrName(null, reqBody.getName());
        if (optionalName.isPresent()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }
        Optional<ParameterTypeEntity> prTypeOptional = parameterTypeRepo.findByCodeOrName(reqBody.getParameterTypeCode(), null);
        if (prTypeOptional.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        ParameterEntity attributeEntity = ParameterEntity.builder()
                .id(UUID.randomUUID().toString())
                .code("PAR000" + (parameterRepo.count() + 1))
                .name(reqBody.getName())
                .parameterTypeCode(prTypeOptional.get().getCode())
                .status(reqBody.getStatus())
                .description(reqBody.getDescription())
                .createdBy(serviceUtils.getUserHeader(httpServletRequest))
                .createdDate(new Timestamp(System.currentTimeMillis()))
                .build();
        parameterRepo.save(attributeEntity);
        return StatusUtil.SUCCESS.name();
    }

    @Override
    public Object postUpdateParameter(PostCreateOrUpdateParameterReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postUpdateParameter reqBody{}", getClass().getSimpleName(), reqBody);

        Optional<ParameterEntity> optionalCode = parameterRepo.findByCodeOrName(reqBody.getCode(), null);
        if (optionalCode.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        Optional<ParameterEntity> optionalName = parameterRepo.findByCodeOrName(null, reqBody.getName());
        if (optionalName.isPresent() && !reqBody.getName().equals(optionalCode.get().getName())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }
        Optional<ParameterTypeEntity> prTypeOptional = parameterTypeRepo.findByCodeOrName(reqBody.getParameterTypeCode(), null);
        if (prTypeOptional.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        ParameterEntity parameterEntity = optionalCode.get();
        modelMapper.map(reqBody, parameterEntity);
        parameterEntity.setUpdatedBy(serviceUtils.getUserHeader(httpServletRequest));
        parameterEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        parameterRepo.save(parameterEntity);
        return StatusUtil.SUCCESS.name();
    }

    @Override
    public PostGetParameterResBody postDetailParameter(PostHandleByCodeParameterReqBody reqBody) throws WarehouseMngtSystemException {
        log.info("{} postDetailParameter reqBody{}", getClass().getSimpleName(), reqBody);
        Optional<ParameterEntity> optionalCode = parameterRepo.findByCodeOrName(reqBody.getCode(), null);
        if (optionalCode.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        return modelMapper.map(optionalCode.get(), PostGetParameterResBody.class);
    }

    @Override
    public Object postDeleteParameter(PostHandleByCodeParameterReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postDeleteParameter reqBody{}", getClass().getSimpleName(), reqBody);
        Optional<ParameterEntity> optionalCode = parameterRepo.findByCodeOrName(reqBody.getCode(), null);
        if (optionalCode.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        ParameterEntity attributeEntity = optionalCode.get();
        attributeEntity.setStatus(StatusUtil.DELETED.name());
        attributeEntity.setUpdatedBy(serviceUtils.getUserHeader(httpServletRequest));
        attributeEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        parameterRepo.save(attributeEntity);
        return StatusUtil.SUCCESS.name();
    }

    private Specification<ParameterEntity> parameterEntitySpec(PostListParameterReqBody request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.notEqual(root.get("status"), StatusUtil.DELETED.name()));
            if (StringUtils.isNotBlank(request.getPrTypeCode())) {
                predicates.add(criteriaBuilder.equal(root.get("parameterTypeCode"), request.getPrTypeCode()));
            }
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
