package com.vworks.wms.admin_service.service;

import com.vworks.wms.admin_service.entity.JobPositionEntity;
import com.vworks.wms.admin_service.model.requestBody.PostCreateJobPositionRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostDeleteJobPositionRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostSearchJobPositionRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostUpdateJobPositionRequestBody;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

public interface JobPositionService {
    Object postCreateJobPosition(PostCreateJobPositionRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    Object postUpdateJobPosition(PostUpdateJobPositionRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    Page<JobPositionEntity> postSearchJobPosition(PostSearchJobPositionRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    Object postDeleteJobPosition(PostDeleteJobPositionRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;
}
