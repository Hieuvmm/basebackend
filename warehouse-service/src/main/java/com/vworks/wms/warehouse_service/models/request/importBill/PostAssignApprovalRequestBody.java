package com.vworks.wms.warehouse_service.models.request.importBill;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostAssignApprovalRequestBody {
    private List<String> importBillCodes;
    private List<String> follows;
    private List<String> approves;
}
