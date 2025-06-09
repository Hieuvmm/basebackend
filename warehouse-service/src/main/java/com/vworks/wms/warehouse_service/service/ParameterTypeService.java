package com.vworks.wms.warehouse_service.service;

import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.warehouse_service.models.request.parameterType.PostCreateOrUpdateParameterTypeReqBody;
import com.vworks.wms.warehouse_service.models.request.parameterType.PostHandleByCodeParameterTypeReqBody;
import com.vworks.wms.warehouse_service.models.request.parameterType.PostListParameterTypeReqBody;
import com.vworks.wms.warehouse_service.models.response.parameterType.PostGetParameterTypeResBody;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

public interface ParameterTypeService {
    Page<PostGetParameterTypeResBody> postListParameterType(PostListParameterTypeReqBody reqBody);

    Object postCreateParameterType(PostCreateOrUpdateParameterTypeReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    Object postUpdateParameterType(PostCreateOrUpdateParameterTypeReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    PostGetParameterTypeResBody postDetailParameterType(PostHandleByCodeParameterTypeReqBody reqBody) throws WarehouseMngtSystemException;

    Object postDeleteParameterType(PostHandleByCodeParameterTypeReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

}
