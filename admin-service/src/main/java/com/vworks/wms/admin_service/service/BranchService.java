package com.vworks.wms.admin_service.service;

import com.vworks.wms.admin_service.model.requestBody.PostCreateBranchRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostDeleteBranchRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostSearchBranchRequestBody;
import com.vworks.wms.admin_service.model.requestBody.PostUpdateBranchRequestBody;
import com.vworks.wms.admin_service.model.responseBody.PostSearchBranchResponseBody;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

public interface BranchService {
    Object postCreateBranch(PostCreateBranchRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    Object postUpdateBranch(PostUpdateBranchRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    Page<PostSearchBranchResponseBody> postSearchBranch(PostSearchBranchRequestBody requestBody, HttpServletRequest httpServletRequest);

    Object postDeleteBranch(PostDeleteBranchRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;
}
