package com.vworks.wms.warehouse_service.service.impl;

import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.utils.StatusUtil;
import com.vworks.wms.warehouse_service.entities.MaterialsEntity;
import com.vworks.wms.warehouse_service.models.request.materialType.*;
import com.vworks.wms.warehouse_service.models.response.materialType.*;
import com.vworks.wms.warehouse_service.repository.MaterialsRepository;
import com.vworks.wms.warehouse_service.service.MaterialTypeService;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialTypeServiceImpl implements MaterialTypeService {
    private final MaterialsRepository materialsRepository;
    private final ModelMapper modelMapper;

    @Override
    public PostCreateMaterialTypeResponse postCreateMaterialType(PostCreateMaterialTypeRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postCreateMaterialType requestBody {}", getClass().getSimpleName(), requestBody);

        Optional<MaterialsEntity> optionalMaterialsCode = materialsRepository.findByCodeOrName(requestBody.getCode(), null);

        if (optionalMaterialsCode.isPresent()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.CODE_EXIST.getCode(), ExceptionTemplate.CODE_EXIST.getMessage());
        }
        Optional<MaterialsEntity> optionalMaterialsName = materialsRepository.findByCodeOrName(null, requestBody.getName());

        if (optionalMaterialsName.isPresent()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }
        MaterialsEntity materialsEntity = MaterialsEntity.builder()
                .id(UUID.randomUUID().toString())
                .code(requestBody.getCode())
                .name(requestBody.getName())
                .description(requestBody.getDescription())
                .status(requestBody.getStatus())
                .createdBy(StringUtils.isBlank(httpServletRequest.getHeader(Commons.USER_CODE_FIELD)) ? httpServletRequest.getHeader(Commons.USER_CODE_FIELD) : null)
                .createdDate(new Timestamp(System.currentTimeMillis()))
                .build();
        materialsRepository.save(materialsEntity);
        return modelMapper.map(materialsEntity, PostCreateMaterialTypeResponse.class);
    }

    @Override
    public PostUpdateMaterialTypeResponse postUpdateMaterialType(PostUpdateMaterialTypeRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postUpdateMaterialType requestBody {}", getClass().getSimpleName(), requestBody);
        Optional<MaterialsEntity> optionalMaterials = materialsRepository.findById(requestBody.getId());
        if (optionalMaterials.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        Optional<MaterialsEntity> optionalMaterialsName = materialsRepository.findByCodeOrName(null, requestBody.getName());

        if (optionalMaterialsName.isPresent() && !StringUtils.equals(optionalMaterialsName.get().getName(), optionalMaterials.get().getName())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }

        Optional<MaterialsEntity> optionalMaterialsCode = materialsRepository.findByCodeOrName(requestBody.getCode(), null);
        if (optionalMaterialsCode.isPresent() && !StringUtils.equals(optionalMaterialsCode.get().getCode(), optionalMaterials.get().getCode())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.CODE_EXIST.getCode(), ExceptionTemplate.CODE_EXIST.getMessage());
        }

        MaterialsEntity materialsEntity = optionalMaterials.get();
        materialsEntity.setName(requestBody.getName());
        materialsEntity.setCode(requestBody.getCode());
        materialsEntity.setStatus(requestBody.getStatus());
        materialsEntity.setDescription(requestBody.getDescription());
        materialsEntity.setUpdatedBy(StringUtils.isBlank(httpServletRequest.getHeader(Commons.USER_CODE_FIELD)) ? httpServletRequest.getHeader(Commons.USER_CODE_FIELD) : null);
        materialsEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        materialsRepository.save(materialsEntity);
        return modelMapper.map(materialsEntity, PostUpdateMaterialTypeResponse.class);
    }

    @Override
    public Page<PostListMaterialTypeResponse> postListMaterialType(PostListMaterialTypeRequest requestBody) {
        log.info("{} postListProvider requestBody {}", getClass().getSimpleName(), requestBody);
        Pageable pageable = PageRequest.of(requestBody.getPage() - 1, requestBody.getLimit(), Sort.by("createdDate").descending());

        Page<MaterialsEntity> page = materialsRepository.findAll(materialsEntitySpecification(requestBody), pageable);

        List<PostListMaterialTypeResponse> list = page.getContent().stream().map(e ->
                modelMapper.map(e, PostListMaterialTypeResponse.class)
        ).toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Override
    public PostDetailMaterialTypeResponse postDetailMaterialType(PostDetailMaterialTypeRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        Optional<MaterialsEntity> optionalProvider = materialsRepository.findById(requestBody.getId());
        if (optionalProvider.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        return modelMapper.map(optionalProvider.get(), PostDetailMaterialTypeResponse.class);
    }

    @Override
    public PostDeleteMaterialTypeResponse postDeleteMaterialType(PostDeleteMaterialTypeRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        Optional<MaterialsEntity> optionalMaterialType = materialsRepository.findById(requestBody.getId());
        if (optionalMaterialType.isEmpty()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        if (StringUtils.equals(optionalMaterialType.get().getStatus(), StatusUtil.ACTIVE.name())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.STATUS_INVALID.getCode(), ExceptionTemplate.STATUS_INVALID.getMessage());
        }
        MaterialsEntity materialsEntity = optionalMaterialType.get();
        materialsEntity.setStatus(StatusUtil.DELETED.name());
        materialsRepository.save(materialsEntity);
        return modelMapper.map(materialsEntity, PostDeleteMaterialTypeResponse.class);
    }

    private Specification<MaterialsEntity> materialsEntitySpecification(PostListMaterialTypeRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.notEqual(root.get("status"), StatusUtil.DELETED.name()));
            if (StringUtils.isNotEmpty(request.getStatus())) {
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
