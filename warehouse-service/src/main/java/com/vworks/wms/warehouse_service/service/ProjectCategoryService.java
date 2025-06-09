package com.vworks.wms.warehouse_service.service;

import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.warehouse_service.models.request.projectCategory.PostDetailProjectCategoryReqBody;
import com.vworks.wms.warehouse_service.models.request.projectCategory.PostListProjectCategoryReqBody;
import com.vworks.wms.warehouse_service.models.request.projectCategory.PostUpdateProjectCategoryReqBody;
import com.vworks.wms.warehouse_service.models.response.projectCategory.PostDetailProjectCategoryResBody;
import com.vworks.wms.warehouse_service.models.response.projectCategory.PostListProjectCategoryResBody;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

public interface ProjectCategoryService {
    Page<PostListProjectCategoryResBody> postListProjectCategory(PostListProjectCategoryReqBody reqBody);

    Object postCreateProjectCategory(PostUpdateProjectCategoryReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    Object postUpdateProjectCategory(PostUpdateProjectCategoryReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    PostDetailProjectCategoryResBody postDetailProjectCategory(PostDetailProjectCategoryReqBody reqBody) throws WarehouseMngtSystemException;

    Object postDeleteProjectCategory(PostDetailProjectCategoryReqBody reqBody) throws WarehouseMngtSystemException;
}
