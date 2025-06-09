package com.vworks.wms.warehouse_service.service.impl;

import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.service.ServiceUtils;
import com.vworks.wms.common_lib.utils.Commons;
import com.vworks.wms.common_lib.utils.StatusUtil;
import com.vworks.wms.warehouse_service.entities.ProjectTypeEntity;
import com.vworks.wms.warehouse_service.models.request.projectType.PostDetailProjectTypeReqBody;
import com.vworks.wms.warehouse_service.models.request.projectType.PostListProjectTypeReqBody;
import com.vworks.wms.warehouse_service.models.request.projectType.PostUpdateProjectTypeReqBody;
import com.vworks.wms.warehouse_service.models.response.projectType.PostDetailProjectTypeResBody;
import com.vworks.wms.warehouse_service.models.response.projectType.PostListProjectTypeResBody;
import com.vworks.wms.warehouse_service.repository.ProjectTypeRepository;
import com.vworks.wms.warehouse_service.service.ProjectTypeService;
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
public class ProjectTypeServiceImpl implements ProjectTypeService {
    private final ProjectTypeRepository projectTypeRepository;
    private final ModelMapper modelMapper;
    private final ServiceUtils serviceUtils;

    @Override
    public Page<PostListProjectTypeResBody> postListProjectType(PostListProjectTypeReqBody reqBody) {
        log.info("{} postListProjectCategory requestBody {}", getClass().getSimpleName(), reqBody);
        Pageable pageable = PageRequest.of(reqBody.getPage() - 1, reqBody.getLimit(), Sort.by(Commons.FIELD_CREATED_DATE).descending());

        Page<ProjectTypeEntity> page = projectTypeRepository.findAll(projectTypeSpec(reqBody), pageable);

        List<PostListProjectTypeResBody> list = page.getContent().stream().map(e ->
                modelMapper.map(e, PostListProjectTypeResBody.class)
        ).toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @Override
    public Object postCreateProjectType(PostUpdateProjectTypeReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postCreateProjectType requestBody {}", getClass().getSimpleName(), reqBody);
        boolean existByCode = projectTypeRepository.existsByCodeOrName(reqBody.getCode(), null);
        if (existByCode) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.CODE_EXIST.getCode(), ExceptionTemplate.CODE_EXIST.getMessage());
        }
        boolean existByName = projectTypeRepository.existsByCodeOrName(null, reqBody.getName());
        if (existByName) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }
        ProjectTypeEntity projectTypeEntity = new ProjectTypeEntity();
        projectTypeEntity.setId(UUID.randomUUID().toString());
        projectTypeEntity.setCode(reqBody.getCode());
        projectTypeEntity.setName(reqBody.getName());
        projectTypeEntity.setStatus(reqBody.getStatus());
        projectTypeEntity.setDescription(reqBody.getDescription());
        projectTypeEntity.setCreatedBy(serviceUtils.getUserHeader(httpServletRequest));
        projectTypeEntity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        projectTypeRepository.save(projectTypeEntity);
        return StatusUtil.SUCCESS;
    }

    @Override
    public Object postUpdateProjectType(PostUpdateProjectTypeReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postUpdateProjectType requestBody {}", getClass().getSimpleName(), reqBody);
        ProjectTypeEntity projectTypeEntity = projectTypeRepository.findByCodeOrName(reqBody.getCode(), null).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        Optional<ProjectTypeEntity> optionalName = projectTypeRepository.findByCodeOrName(null, reqBody.getName());
        if (optionalName.isPresent() && !optionalName.get().getName().equals(projectTypeEntity.getName())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }
        projectTypeEntity.setName(reqBody.getName());
        projectTypeEntity.setStatus(reqBody.getStatus());
        projectTypeEntity.setDescription(reqBody.getDescription());
        projectTypeEntity.setUpdatedBy(serviceUtils.getUserHeader(httpServletRequest));
        projectTypeEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        projectTypeRepository.save(projectTypeEntity);

        return StatusUtil.SUCCESS;
    }

    @Override
    public PostDetailProjectTypeResBody postDetailProjectType(PostDetailProjectTypeReqBody reqBody) throws WarehouseMngtSystemException {
        log.info("{} postDetailProjectType requestBody {}", getClass().getSimpleName(), reqBody);

        ProjectTypeEntity projectTypeEntity = projectTypeRepository.findByCodeOrName(reqBody.getCode(), null).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));
        return modelMapper.map(projectTypeEntity, PostDetailProjectTypeResBody.class);
    }

    @Override
    public Object postDeleteProjectType(PostDetailProjectTypeReqBody reqBody) throws WarehouseMngtSystemException {
        log.info("{} postDeleteProjectType requestBody {}", getClass().getSimpleName(), reqBody);
        ProjectTypeEntity projectTypeEntity = projectTypeRepository.findByCodeOrName(reqBody.getCode(), null).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        if (projectTypeEntity.getStatus().equals(StatusUtil.ACTIVE.name())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.STATUS_INVALID.getCode(), ExceptionTemplate.STATUS_INVALID.getMessage());
        }
        projectTypeEntity.setStatus(StatusUtil.DELETED.name());
        projectTypeRepository.save(projectTypeEntity);
        return StatusUtil.SUCCESS;
    }

    private Specification<ProjectTypeEntity> projectTypeSpec(PostListProjectTypeReqBody request) {
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
