package com.vworks.wms.warehouse_service.service.impl;

import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.utils.StatusUtil;
import com.vworks.wms.warehouse_service.entities.ProviderEntity;
import com.vworks.wms.warehouse_service.models.request.provider.*;
import com.vworks.wms.warehouse_service.models.response.provider.*;
import com.vworks.wms.warehouse_service.repository.ProviderRepository;
import com.vworks.wms.warehouse_service.service.ProviderService;
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
@RequiredArgsConstructor
@Slf4j
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<PostListProviderResponse> postListProvider(PostListProviderRequest requestBody) {
        log.info("{} postListProvider requestBody {}", getClass().getSimpleName(), requestBody);
        Pageable pageable = PageRequest.of(requestBody.getPage() - 1, requestBody.getLimit(), Sort.by("createdDate").descending());

        Page<ProviderEntity> page = providerRepository.findAll(providerEntitySpecification(requestBody), pageable);

        List<PostListProviderResponse> list = page.getContent().stream().map(e ->
                modelMapper.map(e, PostListProviderResponse.class)
        ).toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public PostCreateProviderResponse postCreateProvider(PostCreateProviderRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postCreateProvider requestBody {}", getClass().getSimpleName(), requestBody);

        Optional<ProviderEntity> optionalProviderCode = providerRepository.findByCodeOrName(requestBody.getCode(), null);

        if (optionalProviderCode.isPresent()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.CODE_EXIST.getCode(), ExceptionTemplate.CODE_EXIST.getMessage());
        }
        Optional<ProviderEntity> optionalProviderName = providerRepository.findByCodeOrName(null, requestBody.getName());

        if (optionalProviderName.isPresent()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }
        ProviderEntity providerEntity = ProviderEntity.builder()
                .id(UUID.randomUUID().toString())
                .code(requestBody.getCode())
                .name(requestBody.getName())
                .description(requestBody.getDescription())
                .status(requestBody.getStatus())
                .createdBy(StringUtils.isBlank(httpServletRequest.getHeader(Commons.USER_CODE_FIELD)) ? httpServletRequest.getHeader(Commons.USER_CODE_FIELD) : null)
                .createdDate(new Timestamp(System.currentTimeMillis()))
                .build();
        providerRepository.save(providerEntity);
        return modelMapper.map(providerEntity, PostCreateProviderResponse.class);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public PostUpdateProviderResponse postUpdateProvider(PostUpdateProviderRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        Optional<ProviderEntity> optionalProvider = providerRepository.findById(requestBody.getId());
        if (optionalProvider.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        Optional<ProviderEntity> optionalProviderCode = providerRepository.findByCodeOrName(requestBody.getCode(), null);
        if (optionalProviderCode.isPresent() && !StringUtils.equals(optionalProviderCode.get().getCode(), optionalProvider.get().getCode())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.CODE_EXIST.getCode(), ExceptionTemplate.CODE_EXIST.getMessage());
        }
        Optional<ProviderEntity> optionalProviderName = providerRepository.findByCodeOrName(null, requestBody.getName());

        if (optionalProviderName.isPresent() && !StringUtils.equals(optionalProviderName.get().getName(), optionalProvider.get().getName())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }
        ProviderEntity providerEntity = optionalProvider.get();
        providerEntity.setCode(requestBody.getCode());
        providerEntity.setName(requestBody.getName());
        providerEntity.setDescription(requestBody.getDescription());
        providerEntity.setStatus(requestBody.getStatus());
        providerEntity.setUpdatedBy(StringUtils.isBlank(httpServletRequest.getHeader(Commons.USER_CODE_FIELD)) ? httpServletRequest.getHeader(Commons.USER_CODE_FIELD) : null);
        providerEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        providerRepository.save(providerEntity);
        return modelMapper.map(providerEntity, PostUpdateProviderResponse.class);
    }

    @Override
    public PostDetailProviderResponse postDetailProvider(PostDetailProviderRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        Optional<ProviderEntity> optionalProvider = providerRepository.findById(requestBody.getId());
        if (optionalProvider.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        return modelMapper.map(optionalProvider.get(), PostDetailProviderResponse.class);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public PostDeleteProviderResponse postDeleteProvider(PostDeleteProviderRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        Optional<ProviderEntity> optionalProvider = providerRepository.findById(requestBody.getId());
        if (optionalProvider.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        if (StringUtils.equals(optionalProvider.get().getStatus(), StatusUtil.ACTIVE.name())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.STATUS_INVALID.getCode(), ExceptionTemplate.STATUS_INVALID.getMessage());
        }
        ProviderEntity providerEntity = optionalProvider.get();
        providerEntity.setStatus(StatusUtil.DELETED.name());
        providerRepository.save(providerEntity);
        return modelMapper.map(providerEntity, PostDeleteProviderResponse.class);
    }

    private Specification<ProviderEntity> providerEntitySpecification(PostListProviderRequest request) {
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
