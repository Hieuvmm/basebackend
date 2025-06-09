package com.vworks.wms.warehouse_service.service.impl;

import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.service.ServiceUtils;
import com.vworks.wms.common_lib.utils.Commons;
import com.vworks.wms.common_lib.utils.StatusUtil;
import com.vworks.wms.warehouse_service.entities.ProjectCategoryEntity;
import com.vworks.wms.warehouse_service.models.request.projectCategory.PostDetailProjectCategoryReqBody;
import com.vworks.wms.warehouse_service.models.request.projectCategory.PostListProjectCategoryReqBody;
import com.vworks.wms.warehouse_service.models.request.projectCategory.PostUpdateProjectCategoryReqBody;
import com.vworks.wms.warehouse_service.models.response.projectCategory.PostDetailProjectCategoryResBody;
import com.vworks.wms.warehouse_service.models.response.projectCategory.PostListProjectCategoryResBody;
import com.vworks.wms.warehouse_service.repository.ProjectCategoryRepository;
import com.vworks.wms.warehouse_service.service.ProjectCategoryService;
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
public class ProjectCategoryServiceImpl implements ProjectCategoryService {
    private final ProjectCategoryRepository projectCategoryRepository;
    private final ModelMapper modelMapper;
    private final ServiceUtils serviceUtils;

    @Override
    public Page<PostListProjectCategoryResBody> postListProjectCategory(PostListProjectCategoryReqBody reqBody) {
        log.info("{} postListProjectCategory requestBody {}", getClass().getSimpleName(), reqBody);
        Pageable pageable = PageRequest.of(reqBody.getPage() - 1, reqBody.getLimit(), Sort.by(Commons.FIELD_CREATED_DATE).descending());

        Page<ProjectCategoryEntity> page = projectCategoryRepository.findAll(objectEntitySpecification(reqBody), pageable);

        List<PostListProjectCategoryResBody> list = page.getContent().stream().map(e ->
                modelMapper.map(e, PostListProjectCategoryResBody.class)
        ).toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object postCreateProjectCategory(PostUpdateProjectCategoryReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postCreateProjectCategory requestBody {}", getClass().getSimpleName(), reqBody);
        boolean existByCode = projectCategoryRepository.existsByCodeOrName(reqBody.getCode(), null);
        if (existByCode) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.CODE_EXIST.getCode(), ExceptionTemplate.CODE_EXIST.getMessage());
        }
        boolean existByName = projectCategoryRepository.existsByCodeOrName(null, reqBody.getName());
        if (existByName) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }
        ProjectCategoryEntity projectCategoryEntity = new ProjectCategoryEntity();
        projectCategoryEntity.setId(UUID.randomUUID().toString());
        projectCategoryEntity.setCode(reqBody.getCode());
        projectCategoryEntity.setName(reqBody.getName());
        projectCategoryEntity.setStatus(reqBody.getStatus());
        projectCategoryEntity.setDescription(reqBody.getDescription());
        projectCategoryEntity.setCreatedBy(serviceUtils.getUserHeader(httpServletRequest));
        projectCategoryEntity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        projectCategoryRepository.save(projectCategoryEntity);
        return StatusUtil.SUCCESS;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object postUpdateProjectCategory(PostUpdateProjectCategoryReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postUpdateProjectCategory requestBody {}", getClass().getSimpleName(), reqBody);
        ProjectCategoryEntity projectCategoryEntity = projectCategoryRepository.findByCodeOrName(reqBody.getCode(), null).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        Optional<ProjectCategoryEntity> optionalName = projectCategoryRepository.findByCodeOrName(null, reqBody.getName());
        if (optionalName.isPresent() && !optionalName.get().getName().equals(projectCategoryEntity.getName())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }
        projectCategoryEntity.setName(reqBody.getName());
        projectCategoryEntity.setStatus(reqBody.getStatus());
        projectCategoryEntity.setDescription(reqBody.getDescription());
        projectCategoryEntity.setUpdatedBy(serviceUtils.getUserHeader(httpServletRequest));
        projectCategoryEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        projectCategoryRepository.save(projectCategoryEntity);

        return StatusUtil.SUCCESS;
    }

    @Override
    public PostDetailProjectCategoryResBody postDetailProjectCategory(PostDetailProjectCategoryReqBody reqBody) throws WarehouseMngtSystemException {
        log.info("{} postDetailProjectCategory requestBody {}", getClass().getSimpleName(), reqBody);

        ProjectCategoryEntity projectCategoryEntity = projectCategoryRepository.findByCodeOrName(reqBody.getCode(), null).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));
        return modelMapper.map(projectCategoryEntity, PostDetailProjectCategoryResBody.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Object postDeleteProjectCategory(PostDetailProjectCategoryReqBody reqBody) throws WarehouseMngtSystemException {
        log.info("{} postDeleteProjectCategory requestBody {}", getClass().getSimpleName(), reqBody);
        ProjectCategoryEntity projectCategoryEntity = projectCategoryRepository.findByCodeOrName(reqBody.getCode(), null).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        if (projectCategoryEntity.getStatus().equals(StatusUtil.ACTIVE.name())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.STATUS_INVALID.getCode(), ExceptionTemplate.STATUS_INVALID.getMessage());
        }
        projectCategoryEntity.setStatus(StatusUtil.DELETED.name());
        projectCategoryRepository.save(projectCategoryEntity);
        return StatusUtil.SUCCESS;
    }

    private Specification<ProjectCategoryEntity> objectEntitySpecification(PostListProjectCategoryReqBody request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.notEqual(root.get("status"), StatusUtil.DELETED.name()));
            if (StringUtils.isNotEmpty(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get(Commons.FIELD_STATUS), request.getStatus()));
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
