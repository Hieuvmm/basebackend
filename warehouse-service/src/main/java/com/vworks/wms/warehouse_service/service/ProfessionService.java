package com.vworks.wms.warehouse_service.service;

import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.warehouse_service.models.request.profession.PostCreateOrUpdateProfessionReqBody;
import com.vworks.wms.warehouse_service.models.request.profession.PostHandleByCodeProfessionReqBody;
import com.vworks.wms.warehouse_service.models.request.profession.PostListProfessionReqBody;
import com.vworks.wms.warehouse_service.models.response.profession.PostGetProfessionResBody;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

public interface ProfessionService {
    Page<PostGetProfessionResBody> postListProfession(PostListProfessionReqBody reqBody);

    Object postCreateProfession(PostCreateOrUpdateProfessionReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    Object postUpdateProfession(PostCreateOrUpdateProfessionReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    PostGetProfessionResBody postDetailProfession(PostHandleByCodeProfessionReqBody reqBody) throws WarehouseMngtSystemException;

    Object postDeleteProfession(PostHandleByCodeProfessionReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;
}
