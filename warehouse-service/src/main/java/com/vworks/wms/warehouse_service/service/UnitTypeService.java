package com.vworks.wms.warehouse_service.service;

import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.warehouse_service.models.request.unitType.*;
import com.vworks.wms.warehouse_service.models.response.unitType.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

public interface UnitTypeService {
    PostCreateUnitTypeResponse postCreateUnitType(PostCreateUnitTypeRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    PostUpdateUnitTypeResponse postUpdateUnitType(PostUpdateUnitTypeRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    Page<PostListUnitTypeResponse> postListUnitType(PostListUnitTypeRequest requestBody);

    PostDetailUnitTypeResponse postDetailUnitType(PostDetailUnitTypeRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    PostDeleteUnitTypeResponse postDeleteUnitType(PostDeleteUnitTypeRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;
}
