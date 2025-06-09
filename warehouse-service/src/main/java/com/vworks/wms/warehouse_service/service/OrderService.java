package com.vworks.wms.warehouse_service.service;

import com.vworks.wms.common_lib.base.BaseResponse;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.warehouse_service.entities.OrderEntity;
import com.vworks.wms.warehouse_service.models.request.order.*;
import com.vworks.wms.warehouse_service.models.response.order.PostDetailOrderResBody;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

public interface OrderService {
    Page<OrderEntity> postListOrder(PostListOrderReqBody reqBody);

    Object postCreateOrder(PostCreateOrderReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    Object postUpdateOrder(PostUpdateOrderReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    PostDetailOrderResBody postDetailOrder(PostDetailOrderReqBody reqBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    BaseResponse<?> postUpdateStatusOrder(PostUpdateStatusOrderRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    BaseResponse<?> postAssignApprovalOrder(PostAssignApprovalRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    BaseResponse<?> postApprovedOrder(PostApprovedOrderRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;
}
