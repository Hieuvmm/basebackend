package com.vworks.wms.warehouse_service.models.request.exportBill;

import lombok.Data;

@Data
public class PostApprovalExBillRequestBody {
    private String exCode;
    private String status;
    private String reason;
}
