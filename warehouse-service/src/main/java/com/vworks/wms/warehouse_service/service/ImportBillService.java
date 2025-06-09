package com.vworks.wms.warehouse_service.service;

import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.warehouse_service.models.request.importBill.*;
import com.vworks.wms.warehouse_service.models.response.importBill.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

public interface ImportBillService {
    Page<PostListImportBillResponseBody> postListImportBill(PostListImportBillRequestBody postListImportBillRequestBody);

    PostCreateImportBillResponseBody postCreateImportBill(PostCreateImportBillRequestBody postCreateImportBillRequestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    PostAssignApprovalResponseBody postAssignApprovalImportBill(PostAssignApprovalRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    PostChangeStatusImportBillResponseBody postChangeStatusImportBill(PostChangeStatusImportBillRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    PostUpdateImportBillResponseBody postUpdateImportBill(PostUpdateImportBillRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    PostDetailImportBillResponseBody postDetailImportBill(PostDetailImportBillRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;

    PostApproveImportBillResponseBody postApproveImportBill(PostApproveImportBillRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException;
}
