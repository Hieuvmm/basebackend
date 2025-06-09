package com.vworks.wms.warehouse_service.models.request.order;

import lombok.Data;

import java.util.List;

@Data
public class PostAssignApprovalRequestBody {
    private List<String> orderCodeList;
    private List<String> follows;
    private List<String> approves;
}
