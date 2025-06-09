package com.vworks.wms.warehouse_service.service;

import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.warehouse_service.models.request.material.*;
import com.vworks.wms.warehouse_service.models.response.material.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MaterialService {

    PostCreateMaterialResponse postCreateMaterial(PostCreateMaterialRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    PostUpdateMaterialResponse postUpdateMaterial(PostUpdateMaterialRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    Page<PostListMaterialResponse> postListMaterial(PostListMaterialRequest requestBody, HttpServletRequest httpServletRequest);

    PostDetailMaterialResponse postDetailMaterial(PostDetailMaterialRequest requestBody) throws WarehouseMngtSystemException;

    PostDeleteMaterialResponse postDeleteMaterial(PostDeleteMaterialRequest requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    List<PostGetByConditionResponseBody> postGetByCondition(PostGetByConditionRequestBody responseBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    Object postDetailMaterialList(PostDetailMaterialListRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    List<ParameterModel> mapParameter(String e);

    DiscountMaterialModel getDiscountModel(String discount, String username);
}
