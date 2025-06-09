package com.vworks.wms.admin_service.service.impl;

import com.google.gson.Gson;
import com.vworks.wms.admin_service.entity.JobPositionEntity;
import com.vworks.wms.admin_service.model.requestBody.PostCreateJobPositionRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostDeleteJobPositionRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostSearchJobPositionRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostUpdateJobPositionRequestBody;
import com.vworks.wms.admin_service.repository.JobPositionRepository;
import com.vworks.wms.admin_service.service.JobPositionService;
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
@RequiredArgsConstructor
@Slf4j
public class JobPositionServiceImpl implements JobPositionService {
    private final JobPositionRepository jobPositionRepository;
    private final ServiceUtils serviceUtils;

    @Override
    public Object postCreateJobPosition(PostCreateJobPositionRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("[START] {} postCreateJobPosition with request body = {}",
                this.getClass().getSimpleName(), new Gson().toJson(requestBody));
        JobPositionEntity jobPositionEntity = new JobPositionEntity();
        if (StringUtils.isEmpty(requestBody.getCode()) || StringUtils.isEmpty(requestBody.getName())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.REQUEST_INVALID.getCode(), ASExceptionTemplate.REQUEST_INVALID.getMessage());
        }
        JobPositionEntity jobPositionEntityByCode = jobPositionRepository.findFirstByCodeOrId(requestBody.getCode(), null);

        if (!Objects.isNull(jobPositionEntityByCode)) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.JP_CODE_INVALID.getCode(), ASExceptionTemplate.JP_CODE_INVALID.getMessage());
        }
        jobPositionEntity.setId(UUID.randomUUID().toString());
        jobPositionEntity.setCode(requestBody.getCode());
        jobPositionEntity.setName(requestBody.getName());
        jobPositionEntity.setStatus(requestBody.getStatus());
        if (StringUtils.isNotEmpty(requestBody.getStatus())) {
            jobPositionEntity.setStatus(StatusUtil.ACTIVE.name());
        }

        if (StringUtils.isNotEmpty(requestBody.getDesc())) {
            jobPositionEntity.setDescription(requestBody.getDesc());
        }

        jobPositionEntity.setCreatedBy("sys");
        jobPositionEntity.setCreatedDate(new Timestamp(System.currentTimeMillis()));

        jobPositionRepository.save(jobPositionEntity);
        log.info("[END] {} createJobPosition response with entity = {}",
                this.getClass().getSimpleName(), new Gson().toJson(jobPositionEntity));
        return StatusUtil.SUCCESS;
    }

    @Override
    public Object postUpdateJobPosition(PostUpdateJobPositionRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("[START] {} postUpdateJobPosition with request body = {}",
                this.getClass().getSimpleName(), new Gson().toJson(requestBody));

        if (StringUtils.isEmpty(requestBody.getCode())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.JP_CODE_EMPTY.getCode(), ASExceptionTemplate.JP_CODE_EMPTY.getMessage());
        }

        JobPositionEntity jobPositionEntity = jobPositionRepository.findFirstByCodeOrId(requestBody.getCode(), null);

        if (Objects.isNull(jobPositionEntity)) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.JP_NOT_FOUND.getCode(), ASExceptionTemplate.JP_NOT_FOUND.getMessage());
        }

        if (StringUtils.isNotEmpty(requestBody.getName())) {
            jobPositionEntity.setName(requestBody.getName());
        }

        if (StringUtils.isNotEmpty(requestBody.getStatus())) {
            jobPositionEntity.setStatus(requestBody.getStatus());
        }

        if (StringUtils.isNotEmpty(requestBody.getDesc())) {
            jobPositionEntity.setDescription(requestBody.getDesc());
        }
        jobPositionRepository.save(jobPositionEntity);
        log.info("[END] {} postUpdateJobPosition response with entity = {}",
                this.getClass().getSimpleName(), new Gson().toJson(jobPositionEntity));
        return StatusUtil.SUCCESS;
    }

    @Override
    public Page<JobPositionEntity> postSearchJobPosition(PostSearchJobPositionRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("[START] {} postSearchJobPosition with request = {}",
                this.getClass().getSimpleName(), new Gson().toJson(requestBody));
        Pageable pageable = serviceUtils.handlePageable(requestBody.getPage(), requestBody.getLimit(), requestBody.getOrders());

        Specification<JobPositionEntity> jobPositionEntitySpecification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotEmpty(requestBody.getStatus())) {
                predicates.add(builder.equal(root.get(Commons.FIELD_STATUS), requestBody.getStatus()));
            }

            if (StringUtils.isNotEmpty(requestBody.getSearchText())) {
                String searchValue = "%" + requestBody.getSearchText() + "%";
                predicates.add(builder.like(builder.lower(root.get(Commons.FIELD_CODE)), searchValue));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };

        Page<JobPositionEntity> jobTitleEntityPage = jobPositionRepository.findAll(jobPositionEntitySpecification, pageable);

        if (jobTitleEntityPage.isEmpty() || CollectionUtils.isEmpty(jobTitleEntityPage.getContent())) {
            return new PageImpl<>(new ArrayList<>(), pageable, jobTitleEntityPage.getTotalElements());
        }

        return new PageImpl<>(jobTitleEntityPage.getContent(), pageable, jobTitleEntityPage.getTotalElements());
    }

    @Override
    public Object postDeleteJobPosition(PostDeleteJobPositionRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postDeleteJobPosition with requestBody {} ", getClass().getSimpleName(), requestBody);
        JobPositionEntity jobPositionEntity = jobPositionRepository.findFirstByCodeOrId(requestBody.getCode(), null);
        if (Objects.isNull(jobPositionEntity)) {
            log.error("{} postDeleteJobPosition Job position data isn't exist! ", getClass().getSimpleName());
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.DATA_NOT_FOUND.getCode(), ASExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        String updatedBy = httpServletRequest.getHeader(Commons.FIELD_USER_CODE);
        jobPositionEntity.setStatus(StatusUtil.DELETED.name());
        jobPositionEntity.setUpdatedDate(currentTime);
        jobPositionEntity.setUpdatedBy(updatedBy);

        jobPositionRepository.save(jobPositionEntity);
        return StatusUtil.SUCCESS.name();
    }
}
