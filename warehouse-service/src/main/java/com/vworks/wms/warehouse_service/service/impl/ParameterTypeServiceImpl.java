package com.vworks.wms.warehouse_service.service.impl;

import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.service.ServiceUtils;
import com.vworks.wms.common_lib.utils.StatusUtil;
import com.vworks.wms.warehouse_service.entities.ParameterTypeEntity;
import com.vworks.wms.warehouse_service.models.request.parameterType.PostCreateOrUpdateParameterTypeReqBody;
import com.vworks.wms.warehouse_service.models.request.parameterType.PostHandleByCodeParameterTypeReqBody;
import com.vworks.wms.warehouse_service.models.request.parameterType.PostListParameterTypeReqBody;
import com.vworks.wms.warehouse_service.models.response.parameterType.PostGetParameterTypeResBody;
import com.vworks.wms.warehouse_service.repository.ParameterTypeRepository;
import com.vworks.wms.warehouse_service.service.ParameterTypeService;
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
public class ParameterTypeServiceImpl implements ParameterTypeService {
    private final ParameterTypeRepository parameterTypeRepo;
    private final ModelMapper modelMapper;
    private final ServiceUtils serviceUtils;

    @Override
    public Page<PostGetParameterTypeResBody> postListParameterType(PostListParameterTypeReqBody reqBody) {
        log.info("{} postListParameterType reqBody{}", getClass().getSimpleName(), reqBody);
        Pageable pageable = PageRequest.of(reqBody.getPage() - 1, reqBody.getLimit(), Sort.by("createdDate").descending());

        Page<ParameterTypeEntity> page = parameterTypeRepo.findAll(parameterTypeEntitySpec(reqBody), pageable);

        List<PostGetParameterTypeResBody> list = page.getContent().stream().map(e ->
                modelMapper.map(e, PostGetParameterTypeResBody.class)
        ).toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object postCreateParameterType(PostCreateOrUpdateParameterTypeReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postCreateParameterType reqBody{}", getClass().getSimpleName(), reqBody);

        boolean exists = parameterTypeRepo.findByCodeOrName(null, reqBody.getName()).isPresent();
        if (exists) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }

        ParameterTypeEntity parameterTypeEntity = ParameterTypeEntity.builder()
                .id(UUID.randomUUID().toString())
                .code("PRM_TYPE000" + (parameterTypeRepo.count() + 1))
                .name(reqBody.getName())
                .description(reqBody.getDescription())
                .status(reqBody.getStatus())
                .createdBy(serviceUtils.getUserHeader(httpServletRequest))
                .createdDate(new Timestamp(System.currentTimeMillis()))
                .build();
        parameterTypeRepo.save(parameterTypeEntity);
        return StatusUtil.SUCCESS.name();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object postUpdateParameterType(PostCreateOrUpdateParameterTypeReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postUpdateParameterType reqBody{}", getClass().getSimpleName(), reqBody);

        Optional<ParameterTypeEntity> optionalCode = parameterTypeRepo.findByCodeOrName(reqBody.getCode(), null);
        if (optionalCode.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        Optional<ParameterTypeEntity> optionalName = parameterTypeRepo.findByCodeOrName(null, reqBody.getName());
        if (optionalName.isPresent() && !reqBody.getName().equals(optionalCode.get().getName())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }
        ParameterTypeEntity parameterTypeEntity = optionalCode.get();
        modelMapper.map(reqBody, parameterTypeEntity);
        parameterTypeEntity.setUpdatedBy(serviceUtils.getUserHeader(httpServletRequest));
        parameterTypeEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        parameterTypeRepo.save(parameterTypeEntity);
        return StatusUtil.SUCCESS.name();
    }

    @Override
    public PostGetParameterTypeResBody postDetailParameterType(PostHandleByCodeParameterTypeReqBody reqBody) throws WarehouseMngtSystemException {
        log.info("{} postDetailParameterType reqBody{}", getClass().getSimpleName(), reqBody);
        Optional<ParameterTypeEntity> optionalCode = parameterTypeRepo.findByCodeOrName(reqBody.getCode(), null);
        if (optionalCode.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        return modelMapper.map(optionalCode.get(), PostGetParameterTypeResBody.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object postDeleteParameterType(PostHandleByCodeParameterTypeReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postDeleteParameterType reqBody{}", getClass().getSimpleName(), reqBody);
        Optional<ParameterTypeEntity> optionalCode = parameterTypeRepo.findByCodeOrName(reqBody.getCode(), null);
        if (optionalCode.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        ParameterTypeEntity parameterTypeEntity = optionalCode.get();
        parameterTypeEntity.setStatus(StatusUtil.DELETED.name());
        parameterTypeEntity.setUpdatedBy(serviceUtils.getUserHeader(httpServletRequest));
        parameterTypeEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        parameterTypeRepo.save(parameterTypeEntity);
        return StatusUtil.SUCCESS.name();
    }

    private Specification<ParameterTypeEntity> parameterTypeEntitySpec(PostListParameterTypeReqBody request) {
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
