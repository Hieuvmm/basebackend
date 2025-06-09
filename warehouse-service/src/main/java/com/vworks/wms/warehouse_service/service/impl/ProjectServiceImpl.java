package com.vworks.wms.warehouse_service.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vworks.wms.admin_service.entity.UserInfoEntity;
import com.vworks.wms.admin_service.repository.UserInfoRepository;
import com.vworks.wms.common_lib.config.MinioConfigProperties;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.common_lib.service.MinioService;
import com.vworks.wms.common_lib.service.ServiceUtils;
import com.vworks.wms.common_lib.utils.Commons;
import com.vworks.wms.common_lib.utils.StatusUtil;
import com.vworks.wms.warehouse_service.entities.ObjectEntity;
import com.vworks.wms.warehouse_service.entities.ProjectEntity;
import com.vworks.wms.warehouse_service.entities.ProjectTypeEntity;
import com.vworks.wms.warehouse_service.models.ApprovedDetailModel;
import com.vworks.wms.warehouse_service.models.request.CategoryInProject;
import com.vworks.wms.warehouse_service.models.request.OtherInfoInProject;
import com.vworks.wms.warehouse_service.models.request.project.*;
import com.vworks.wms.warehouse_service.models.response.project.PostDetailProjectResBody;
import com.vworks.wms.warehouse_service.models.response.project.PostListProjectResBody;
import com.vworks.wms.warehouse_service.models.response.project.ProjectCategoryItem;
import com.vworks.wms.warehouse_service.repository.ObjectRepository;
import com.vworks.wms.warehouse_service.repository.ProjectRepository;
import com.vworks.wms.warehouse_service.repository.ProjectTypeRepository;
import com.vworks.wms.warehouse_service.service.ProjectService;
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
import org.springframework.util.ObjectUtils;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ServiceUtils serviceUtils;
    private final ProjectTypeRepository projectTypeRepository;
    private final UserInfoRepository userInfoRepository;
    private final ObjectRepository objectRepository;
    private final ModelMapper modelMapper;
    private final Gson gson;
    private final MinioService minioService;
    private final MinioConfigProperties minioConfigProperties;

    @Override
    public Page<PostListProjectResBody> postListProject(PostListProjectReqBody reqBody) {
        log.info("{} postListProject reqBody {}", getClass().getSimpleName(), reqBody);

        Pageable pageable = PageRequest.of(reqBody.getPage() - 1, reqBody.getLimit(), Sort.by(Commons.FIELD_CREATED_DATE).descending());

        Page<ProjectEntity> page = projectRepository.findAll(projectSpec(reqBody), pageable);
        List<UserInfoEntity> userInfos = userInfoRepository.findByUserIdIn(page.getContent().stream().map(ProjectEntity::getSupervisorCode).toList());
        List<ProjectTypeEntity> projectTypes = projectTypeRepository.findAllByCodeIn(page.getContent().stream().map(ProjectEntity::getProjectTypeCode).toList());
        ObjectMapper objectMapper = new ObjectMapper();

        List<PostListProjectResBody> list = page.getContent().stream().map(e ->
                PostListProjectResBody.builder()
                        .code(e.getCode())
                        .name(e.getName())
                        .approvals(e.getApproval())
                        .approvals(e.getApproval())
                        .customerCode(e.getCustomerCode())
                        .addressDetail(e.getAddressDetail())
                        .mainCategory(e.getCategoryInfo())
                        .subItemCount(getTotalMaterialQuantity(e.getCategoryInfo(), objectMapper))
                        .technicianCode(e.getTechnicianCode())
                        .projectType(projectTypes.stream().filter(f -> StringUtils.equals(e.getCode(), f.getCode())).findFirst().orElse(null))
                        .supervisor(userInfos.stream().filter(f -> StringUtils.equals(e.getSupervisorCode(), f.getUserId())).findFirst().orElse(null))
                        .endDate(e.getEndDate())
                        .startDate(e.getStartDate())
                        .status(e.getStatus())
                        .build()
        ).toList();
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    private int getTotalMaterialQuantity(String categoryInfoJson, ObjectMapper objectMapper) {
        try {
            if (categoryInfoJson == null || categoryInfoJson.trim().isEmpty()) {
                return 0;
            }

            List<ProjectCategoryItem> items = objectMapper.readValue(
                    categoryInfoJson,
                    new TypeReference<List<ProjectCategoryItem>>() {}
            );

            return items.stream()
                    .mapToInt(ProjectCategoryItem::getMaterialQuantity)
                    .sum();
        } catch (Exception e) {
            log.warn("Lỗi parse category_info: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public Object postCreateProject(PostUpdateProjectReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postCreateProject reqBody {}", getClass().getSimpleName(), reqBody);

        boolean existByCode = projectRepository.existsByCodeOrName(reqBody.getCode(), null);
        if (existByCode) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.CODE_EXIST.getCode(), ExceptionTemplate.CODE_EXIST.getMessage());
        }

        boolean existByName = projectRepository.existsByCodeOrName(null, reqBody.getName());
        if (existByName) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }

        boolean projectExist = projectTypeRepository.existsByCodeOrName(reqBody.getProjectTypeCode(), null);
        if (!projectExist) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        boolean technicianExist = userInfoRepository.existsByUserId(reqBody.getTechnicianCode());
        if (!technicianExist) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        ObjectEntity objectEntity = objectRepository.findByCodeOrName(reqBody.getCustomerCode(), null).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        if (!objectEntity.getType().contains(Commons.FIELD_CUSTOMER_TYPE)) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.OBJECT_INVALID.getCode(), ExceptionTemplate.OBJECT_INVALID.getMessage());
        }
        String pathFile = minioConfigProperties.getProjectAttachmentsFolderStorage() + reqBody.getCode() + "_" + reqBody.getName();
        String fileName = ObjectUtils.isEmpty(reqBody.getAttachments()) ? null : minioService.uploadFileToMinio(reqBody.getAttachments(), minioConfigProperties.getBucketName(), pathFile);
        ProjectEntity projectEntity = new ProjectEntity();
        modelMapper.map(reqBody, projectEntity);
        projectEntity.setId(UUID.randomUUID().toString());
        projectEntity.setCreatedBy(serviceUtils.getUserHeader(httpServletRequest));
        projectEntity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        projectEntity.setOtherInfo(gson.toJson(reqBody.getOtherInfo()));
        projectEntity.setCategoryInfo(gson.toJson(reqBody.getCategoryInfo()));
        projectEntity.setStatus(StatusUtil.CREATED.name());
        if (Objects.nonNull(fileName)) {
            projectEntity.setAttachments(fileName);
        }
        projectRepository.save(projectEntity);
        return StatusUtil.SUCCESS;
    }

    @Override
    public Object postUpdateProject(PostUpdateProjectReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {

        ProjectEntity projectEntity = projectRepository.findByCodeOrName(null, reqBody.getName()).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_EXISTED.getMessage()));

        boolean existByName = projectRepository.existsByCodeOrName(null, reqBody.getName());
        if (existByName && !StringUtils.equals(projectEntity.getName(), reqBody.getName())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.NAME_EXIST.getCode(), ExceptionTemplate.NAME_EXIST.getMessage());
        }

        boolean projectExist = projectTypeRepository.existsByCodeOrName(reqBody.getProjectTypeCode(), null);
        if (!projectExist) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        boolean supervisorExist = userInfoRepository.existsByUserId(reqBody.getSupervisorCode());
        if (!supervisorExist) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        boolean technicianExist = userInfoRepository.existsByUserId(reqBody.getTechnicianCode());
        if (!technicianExist) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        ObjectEntity objectEntity = objectRepository.findByCodeOrName(reqBody.getCustomerCode(), null).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        if (!objectEntity.getType().contains(Commons.FIELD_CUSTOMER_TYPE)) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.OBJECT_INVALID.getCode(), ExceptionTemplate.OBJECT_INVALID.getMessage());
        }
        String pathFile = minioConfigProperties.getProjectAttachmentsFolderStorage() + "/" + reqBody.getCode();
        String fileName = ObjectUtils.isEmpty(reqBody.getAttachments()) ? null : minioService.uploadFileToMinio(reqBody.getAttachments(), minioConfigProperties.getBucketName(), pathFile);

        modelMapper.map(reqBody, projectEntity);
        projectEntity.setUpdatedBy(serviceUtils.getUserHeader(httpServletRequest));
        projectEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        projectEntity.setOtherInfo(gson.toJson(reqBody.getOtherInfo()));
        projectEntity.setCategoryInfo(gson.toJson(reqBody.getCategoryInfo()));
        if (Objects.nonNull(fileName)) {
            projectEntity.setAttachments(fileName);
        }
        projectRepository.save(projectEntity);
        return StatusUtil.SUCCESS;
    }

    @Override
    public PostDetailProjectResBody postDetailProject(PostDetailProjectReqBody reqBody) throws WarehouseMngtSystemException {
        log.info("{} postDetailProject reqBody {}", getClass().getSimpleName(), reqBody);
        ProjectEntity projectEntity = projectRepository.findByCodeOrName(reqBody.getCode(), null)
                .orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));
        PostDetailProjectResBody projectResBody = new PostDetailProjectResBody();
        modelMapper.map(projectEntity, projectResBody);
        projectResBody.setOtherInfo(gson.fromJson(projectEntity.getOtherInfo(), new TypeToken<List<OtherInfoInProject>>() {
        }.getType()));
        projectResBody.setCategoryInfo(gson.fromJson(projectEntity.getCategoryInfo(), new TypeToken<List<CategoryInProject>>() {
        }.getType()));

        return projectResBody;
    }

    @Override
    public Object postDeleteProject(PostDetailProjectReqBody reqBody) throws WarehouseMngtSystemException {
        log.info("{} postDeleteProject reqBody {}", getClass().getSimpleName(), reqBody);
        ProjectEntity projectEntity = projectRepository.findByCodeOrName(reqBody.getCode(), null)
                .orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        if (projectEntity.getStatus().equals(StatusUtil.ACTIVE.name())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.STATUS_INVALID.getCode(), ExceptionTemplate.STATUS_INVALID.getMessage());
        }
        projectEntity.setStatus(StatusUtil.DELETED.name());
        projectRepository.save(projectEntity);
        return StatusUtil.SUCCESS;
    }

    @Override
    public Object postAssignApprovalProject(PostAssignProjectReqBody reqBody) throws WarehouseMngtSystemException {
        log.info("{} postAssignApprovalProject reqBody {}", getClass().getSimpleName(), reqBody);

        List<ProjectEntity> projects = projectRepository.findAllByCodeIn(reqBody.getProjectCodes());
        if (projects.size() != reqBody.getProjectCodes().size()) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }
        boolean statusInvalid = projects.stream().anyMatch(e -> !StringUtils.equals(e.getStatus(), StatusUtil.CREATED.name()));
        if (statusInvalid) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.STATUS_INVALID.getCode(), ExceptionTemplate.STATUS_INVALID.getMessage());
        }
        projects = projects.stream().peek(e -> {
            e.setApproval(String.join(",", reqBody.getApproves()));
            e.setStatus(StatusUtil.REVIEWING.name());
        }).toList();
        projectRepository.saveAll(projects);
        return StatusUtil.SUCCESS;
    }

    @Override
    public Object postApproveProject(PostApproveProjectReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postApproveProject reqBody {}", getClass().getSimpleName(), reqBody);

        UserInfoEntity userInfo = userInfoRepository.findByUserId(reqBody.getUserId()).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));

        ProjectEntity project = projectRepository.findByCodeOrName(reqBody.getProjectCode(), null).orElseThrow(() -> new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ExceptionTemplate.DATA_NOT_FOUND.getCode(), ExceptionTemplate.DATA_NOT_FOUND.getMessage()));
        if (reqBody.getStatus().equals(StatusUtil.APPROVED.name()) && !project.getApproval().contains(reqBody.getUserId())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(),
                    ExceptionTemplate.APPROVAL_INVALID.getCode(),
                    ExceptionTemplate.APPROVAL_INVALID.getMessage());
        }
        checkStatus(reqBody, project, userInfo);
        projectRepository.save(project);
        return StatusUtil.SUCCESS;
    }

    private void checkStatus(PostApproveProjectReqBody reqBody, ProjectEntity project, UserInfoEntity userInfo) throws WarehouseMngtSystemException {
        log.info("{} checkStatus reqBody {}", getClass().getSimpleName(), reqBody);

        Map<String, String> validStatusTransitions = new HashMap<>();
        validStatusTransitions.put(StatusUtil.APPROVED.name(), StatusUtil.REVIEWING.name());
        validStatusTransitions.put(StatusUtil.REFUSED.name(), StatusUtil.REVIEWING.name());
        validStatusTransitions.put(StatusUtil.HANDLING.name(), StatusUtil.APPROVED.name());
        validStatusTransitions.put(StatusUtil.GO_ON.name(), StatusUtil.PAUSED.name());
        validStatusTransitions.put(StatusUtil.CANCELED.name(), StatusUtil.HANDLING.name());
        validStatusTransitions.put(StatusUtil.DONE.name(), StatusUtil.HANDLING.name());
        validStatusTransitions.put(StatusUtil.PAUSED.name(), StatusUtil.HANDLING.name());
        if (validStatusTransitions.containsKey(reqBody.getStatus()) && validStatusTransitions.get(reqBody.getStatus()).equals(project.getStatus())) {
            ApprovedDetailModel approvedDetailModel = ApprovedDetailModel.builder()
                    .userId(userInfo.getUserId())
                    .status(reqBody.getStatus())
                    .approveTime(serviceUtils.convertTimeStampToString(new Timestamp(System.currentTimeMillis())))
                    .userName(userInfo.getFullName())
                    .note(reqBody.getNote())
                    .build();
            List<ApprovedDetailModel> approvedDetailModels = new ArrayList<>();
            if (StringUtils.isNotEmpty(project.getActionDetail())) {
                approvedDetailModels = gson.fromJson(project.getActionDetail(), new TypeToken<List<ApprovedDetailModel>>() {
                }.getType());

            }
            approvedDetailModels.add(approvedDetailModel);
            project.setActionDetail(gson.toJson(approvedDetailModels));
            project.setStatus(reqBody.getStatus().equals(StatusUtil.GO_ON.name()) ? StatusUtil.HANDLING.name() : reqBody.getStatus());
        } else {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(),
                    ExceptionTemplate.STATUS_INVALID.getCode(),
                    ExceptionTemplate.STATUS_INVALID.getMessage());
        }
    }

    private Specification<ProjectEntity> projectSpec(PostListProjectReqBody request) {
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
