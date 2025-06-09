package com.vworks.wms.warehouse_service.models.request.order;

import lombok.Data;

@Data
public class PostApprovedOrderRequestBody {
    private String orderCode;
    private String status;
    private String reason;
}
