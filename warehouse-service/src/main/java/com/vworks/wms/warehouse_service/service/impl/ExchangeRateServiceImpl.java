package com.vworks.wms.warehouse_service.service.impl;

import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.utils.StatusUtil;
import com.vworks.wms.warehouse_service.entities.ExchangeRateEntity;
import com.vworks.wms.warehouse_service.models.request.exchangeRate.*;
import com.vworks.wms.warehouse_service.models.response.exchangeRate.*;
import com.vworks.wms.warehouse_service.repository.ExchangeRateRepository;
import com.vworks.wms.warehouse_service.service.ExchangeRateService;
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
public class ExchangeRateServiceImpl implements ExchangeRateService {
    private final ExchangeRateRepository exchangeRateRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<PostListExchangeRateResponse> postListExchangeRate(PostListExchangeRateRequest requestBody) {
        log.info("{} postListExchangeRate requestBody {}", getClass().getSimpleName(), requestBody);
        Pageable pageable = PageRequest.of(requestBody.getPage() - 1, requestBody.getLimit(), Sort.by("createdDate").descending());

        Page<ExchangeRateEntity> page = exchangeRateRepository.findAll(exchangeRateSpecification(requestBody), pageable);

        List<PostListExchangeRateResponse> list = page.getContent().stream().map(e ->
                PostListExchangeRateResponse.builder()
                        .id(e.getId())
                        .name(e.getName())
                        .code(e.getCode())
                        .value(e.getValue())
                        .status(e.getStatus())
                        .exchangeType(exchangeRateRepository.findByCodeOrName(e.getExchangeType(), null).map(ExchangeRateEntity::getName).orElse(""))
                        .build()
        ).toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public PostCreateExchangeRateResponse postCreateExchangeRate(PostCreateExchangeRateRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postCreateExchangeRate requestBody {}", getClass().getSimpleName(), requestBody);

        Optional<ExchangeRateEntity> optionalExchangeRateCode = exchangeRateRepository.findByCodeOrName(requestBody.getCode(), null);

        if (optionalExchangeRateCode.isPresent()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.CODE_EXIST.getCode(), ExceptionTemplate.CODE_EXIST.getMessage());
        }
        Optional<ExchangeRateEntity> optionalExchangeRateName = exchangeRateRepository.findByCodeOrName(null, requestBody.getName());

        if (optionalExchangeRateName.isPresent()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }
        ExchangeRateEntity exchangeRateEntity = ExchangeRateEntity.builder()
                .id(UUID.randomUUID().toString())
                .code(requestBody.getCode())
                .name(requestBody.getName())
                .value(requestBody.getValue())
                .exchangeType(requestBody.getExchangeType())
                .status(requestBody.getStatus())
                .createdBy(StringUtils.isBlank(httpServletRequest.getHeader(Commons.USER_CODE_FIELD)) ? httpServletRequest.getHeader(Commons.USER_CODE_FIELD) : null)
                .createdDate(new Timestamp(System.currentTimeMillis()))
                .build();
        exchangeRateRepository.save(exchangeRateEntity);
        return modelMapper.map(exchangeRateEntity, PostCreateExchangeRateResponse.class);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public PostUpdateExchangeRateResponse postUpdateExchangeRate(PostUpdateExchangeRateRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        Optional<ExchangeRateEntity> optionalExchangeRate = exchangeRateRepository.findById(requestBody.getId());
        if (optionalExchangeRate.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        Optional<ExchangeRateEntity> optionalExchangeRateCode = exchangeRateRepository.findByCodeOrName(requestBody.getCode(), null);
        if (optionalExchangeRateCode.isPresent() && !StringUtils.equals(optionalExchangeRateCode.get().getCode(), optionalExchangeRate.get().getCode())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.CODE_EXIST.getCode(), ExceptionTemplate.CODE_EXIST.getMessage());
        }
        Optional<ExchangeRateEntity> optionalExchangeRateName = exchangeRateRepository.findByCodeOrName(null, requestBody.getName());

        if (optionalExchangeRateName.isPresent() && !StringUtils.equals(optionalExchangeRateName.get().getName(), optionalExchangeRate.get().getName())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }
        ExchangeRateEntity exchangeRateEntity = optionalExchangeRate.get();
        exchangeRateEntity.setCode(requestBody.getCode());
        exchangeRateEntity.setName(requestBody.getName());
        exchangeRateEntity.setValue(requestBody.getValue());
        exchangeRateEntity.setStatus(requestBody.getStatus());
        exchangeRateEntity.setExchangeType(requestBody.getExchangeType());
        exchangeRateEntity.setUpdatedBy(StringUtils.isBlank(httpServletRequest.getHeader(Commons.USER_CODE_FIELD)) ? httpServletRequest.getHeader(Commons.USER_CODE_FIELD) : null);
        exchangeRateEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        exchangeRateRepository.save(exchangeRateEntity);
        return modelMapper.map(exchangeRateEntity, PostUpdateExchangeRateResponse.class);
    }

    @Override
    public PostDetailExchangeRateResponse postDetailExchangeRate(PostDetailExchangeRateRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        Optional<ExchangeRateEntity> optionalExchangeRate = exchangeRateRepository.findById(requestBody.getId());
        if (optionalExchangeRate.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        return modelMapper.map(optionalExchangeRate.get(), PostDetailExchangeRateResponse.class);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public PostDeleteExchangeRateResponse postDeleteExchangeRate(PostDeleteExchangeRateRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        Optional<ExchangeRateEntity> optionalExchangeRate = exchangeRateRepository.findById(requestBody.getId());
        if (optionalExchangeRate.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        if (StringUtils.equals(optionalExchangeRate.get().getStatus(), StatusUtil.ACTIVE.name())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.STATUS_INVALID.getCode(), ExceptionTemplate.STATUS_INVALID.getMessage());
        }
        ExchangeRateEntity exchangeRateEntity = optionalExchangeRate.get();
        exchangeRateEntity.setStatus(StatusUtil.DELETED.name());
        exchangeRateRepository.save(exchangeRateEntity);
        return modelMapper.map(exchangeRateEntity, PostDeleteExchangeRateResponse.class);
    }

    private Specification<ExchangeRateEntity> exchangeRateSpecification(PostListExchangeRateRequest request) {
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
