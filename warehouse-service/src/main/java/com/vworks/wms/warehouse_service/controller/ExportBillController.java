package com.vworks.wms.warehouse_service.controller;

import com.vworks.wms.common_lib.base.BaseResponse;
import com.vworks.wms.common_lib.exception.WarehouseMngtSystemException;
import com.vworks.wms.warehouse_service.config.WhsConstant;
import com.vworks.wms.warehouse_service.models.request.SearchExBillRequestBody;
import com.vworks.wms.warehouse_service.models.request.exportBill.*;
import com.vworks.wms.warehouse_service.service.ExportBillService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping(WhsConstant.RequestMapping.WHS_WH_EXPORT)
public class ExportBillController {
    private final ExportBillService exportBillService;

    @PostMapping("/create")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'create', this)")
    public BaseResponse<?> createExBillController(@RequestBody CreateExportBillRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        return exportBillService.createExBill(requestBody, httpServletRequest);
    }

    @PostMapping("/search")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'search', this)")
    public BaseResponse<?> searchExBillController(@RequestBody SearchExBillRequestBody requestBody) {
        return new BaseResponse<>(exportBillService.searchExBillV2(requestBody));
    }

    @PostMapping("/approval")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'approve', this)")
    public BaseResponse<?> approvalController(@RequestBody PostApprovalExBillRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        return exportBillService.approvalExBill(requestBody, httpServletRequest);
    }

    @PostMapping("/detail")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'detail', this)")
    public BaseResponse<?> detailController(@RequestBody PostGetDetailExportBillRequestBody requestBody) throws WarehouseMngtSystemException {
        return new BaseResponse<>(exportBillService.deTailExBill(requestBody));
    }

    @PostMapping("/update")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'update', this)")
    public BaseResponse<?> updateController(@RequestBody PostUpdateExportBillRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        return exportBillService.updateExBill(requestBody, httpServletRequest);
    }

    @PostMapping("/delete")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'delete', this)")
    public BaseResponse<?> deleteController(@RequestBody PostDeleteExportBillRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        return exportBillService.deleteExBill(requestBody, httpServletRequest);
    }

    @PostMapping("/send-approval")
    @PreAuthorize("@appAuthorizer.authorize(authentication, 'send-approval', this)")
    public BaseResponse<?> assignApprovalController(@RequestBody PostAssignApprovalRequestBody requestBody, HttpServletRequest httpServletRequest) throws WarehouseMngtSystemException {
        return exportBillService.assignAproval(requestBody, httpServletRequest);
    }
}
