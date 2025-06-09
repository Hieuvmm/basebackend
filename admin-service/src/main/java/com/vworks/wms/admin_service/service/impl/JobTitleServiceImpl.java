package com.vworks.wms.admin_service.service.impl;

import com.google.gson.Gson;
import com.vworks.wms.admin_service.entity.JobTitleEntity;
import com.vworks.wms.admin_service.model.requestBody.PostCreateJobTitleRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostDeleteJobTitleRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostSearchJobTitleRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostUpdateJobTitleRequestBody;
import com.vworks.wms.admin_service.repository.JobTitleRepository;
import com.vworks.wms.admin_service.service.JobTitleService;
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
@Slf4j
@RequiredArgsConstructor
public class JobTitleServiceImpl implements JobTitleService {
    private final JobTitleRepository jobTitleRepository;
    private final ServiceUtils serviceUtils;

    @Override
    public Object postCreateJobTitle(PostCreateJobTitleRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("[START] {} postCreateJobTitle with request body = {}",
                this.getClass().getSimpleName(), new Gson().toJson(requestBody));
        JobTitleEntity jobTitleEntity = new JobTitleEntity();
        if (StringUtils.isEmpty(requestBody.getCode()) || StringUtils.isEmpty(requestBody.getName())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.REQUEST_INVALID.getCode(), ASExceptionTemplate.REQUEST_INVALID.getMessage());
        }
        JobTitleEntity jobTitleEntityByCode = jobTitleRepository.findFirstByCodeOrId(requestBody.getCode(), null);

        if (!Objects.isNull(jobTitleEntityByCode)) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.JP_CODE_INVALID.getCode(), ASExceptionTemplate.JP_CODE_INVALID.getMessage());
        }
        jobTitleEntity.setId(UUID.randomUUID().toString());
        jobTitleEntity.setCode(requestBody.getCode());
        jobTitleEntity.setName(requestBody.getName());
        jobTitleEntity.setStatus(requestBody.getStatus());
        if (StringUtils.isNotEmpty(requestBody.getStatus())) {
            jobTitleEntity.setStatus(StatusUtil.ACTIVE.name());
        }

        if (StringUtils.isNotEmpty(requestBody.getDesc())) {
            jobTitleEntity.setDescription(requestBody.getDesc());
        }

        jobTitleEntity.setCreatedBy("sys");
        jobTitleEntity.setCreatedDate(new Timestamp(System.currentTimeMillis()));

        jobTitleRepository.save(jobTitleEntity);
        log.info("[END] {} createJobTitle response with entity = {}",
                this.getClass().getSimpleName(), new Gson().toJson(jobTitleEntity));
        return StatusUtil.SUCCESS;
    }

    @Override
    public Object postUpdateJobTitle(PostUpdateJobTitleRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("[START] {} postUpdateJobTitle with request body = {}",
                this.getClass().getSimpleName(), new Gson().toJson(requestBody));

        if (StringUtils.isEmpty(requestBody.getCode())) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.JP_CODE_EMPTY.getCode(), ASExceptionTemplate.JP_CODE_EMPTY.getMessage());
        }

        JobTitleEntity jobTitleEntity = jobTitleRepository.findFirstByCodeOrId(requestBody.getCode(), null);

        if (Objects.isNull(jobTitleEntity)) {
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.JP_NOT_FOUND.getCode(), ASExceptionTemplate.JP_NOT_FOUND.getMessage());
        }

        if (StringUtils.isNotEmpty(requestBody.getName())) {
            jobTitleEntity.setName(requestBody.getName());
        }

        if (StringUtils.isNotEmpty(requestBody.getStatus())) {
            jobTitleEntity.setStatus(requestBody.getStatus());
        }

        if (StringUtils.isNotEmpty(requestBody.getDesc())) {
            jobTitleEntity.setDescription(requestBody.getDesc());
        }
        jobTitleRepository.save(jobTitleEntity);
        log.info("[END] {} postUpdateJobTitle response with entity = {}",
                this.getClass().getSimpleName(), new Gson().toJson(jobTitleEntity));
        return StatusUtil.SUCCESS;
    }

    @Override
    public Page<JobTitleEntity> postSearchJobTitle(PostSearchJobTitleRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("[START] {} postSearchJobPosition with request = {}",
                this.getClass().getSimpleName(), new Gson().toJson(requestBody));

        Pageable pageable = serviceUtils.handlePageable(requestBody.getPage(), requestBody.getLimit(), requestBody.getOrders());

        Specification<JobTitleEntity> jobTitleEntitySpecification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotEmpty(requestBody.getSearchText())) {
                String searchValue = "%" + requestBody.getSearchText() + "%";
                predicates.add(builder.like(builder.lower(root.get(Commons.FIELD_CODE)), searchValue));
            }

            if (StringUtils.isNotEmpty(requestBody.getStatus())) {
                predicates.add(builder.equal(root.get(Commons.FIELD_STATUS), requestBody.getStatus()));
            }


            return builder.and(predicates.toArray(new Predicate[0]));
        };

        Page<JobTitleEntity> jobTitleEntityPage = jobTitleRepository.findAll(jobTitleEntitySpecification, pageable);

        if (jobTitleEntityPage.isEmpty() || CollectionUtils.isEmpty(jobTitleEntityPage.getContent())) {
            return new PageImpl<>(new ArrayList<>(), pageable, jobTitleEntityPage.getTotalElements());
        }

        return new PageImpl<>(jobTitleEntityPage.getContent(), pageable, jobTitleEntityPage.getTotalElements());
    }

    @Override
    public Object postDeleteJobTitle(PostDeleteJobTitleRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        log.info("{} postDeleteJobTitle with requestBody {} ", getClass().getSimpleName(), requestBody);
        JobTitleEntity jobTitleEntity = jobTitleRepository.findFirstByCodeOrId(requestBody.getCode(), null);
        if (Objects.isNull(jobTitleEntity)) {
            log.error("{} postDeleteJobTitle Job title data isn't exist! ", getClass().getSimpleName());
            throw new WarehouseMngtSystemException(HttpStatus.BAD_REQUEST.value(), ASExceptionTemplate.DATA_NOT_FOUND.getCode(), ASExceptionTemplate.DATA_NOT_FOUND.getMessage());
        }

        String updatedBy = httpServletRequest.getHeader(Commons.FIELD_USER_CODE);
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        jobTitleEntity.setStatus(StatusUtil.DELETED.name());
        jobTitleEntity.setUpdatedDate(currentTime);
        jobTitleEntity.setUpdatedBy(updatedBy);

        jobTitleRepository.save(jobTitleEntity);
        return StatusUtil.SUCCESS.name();
    }
}
