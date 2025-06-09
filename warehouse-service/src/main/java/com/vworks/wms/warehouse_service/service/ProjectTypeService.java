package com.vworks.wms.warehouse_service.service;

import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.warehouse_service.models.request.projectType.PostDetailProjectTypeReqBody;
import com.vworks.wms.warehouse_service.models.request.projectType.PostListProjectTypeReqBody;
import com.vworks.wms.warehouse_service.models.request.projectType.PostUpdateProjectTypeReqBody;
import com.vworks.wms.warehouse_service.models.response.projectType.PostDetailProjectTypeResBody;
import com.vworks.wms.warehouse_service.models.response.projectType.PostListProjectTypeResBody;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

public interface ProjectTypeService {
    Page<PostListProjectTypeResBody> postListProjectType(PostListProjectTypeReqBody reqBody);

    Object postCreateProjectType(PostUpdateProjectTypeReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    Object postUpdateProjectType(PostUpdateProjectTypeReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    PostDetailProjectTypeResBody postDetailProjectType(PostDetailProjectTypeReqBody reqBody) throws WarehouseMngtSystemException;

    Object postDeleteProjectType(PostDetailProjectTypeReqBody reqBody) throws WarehouseMngtSystemException;
}
