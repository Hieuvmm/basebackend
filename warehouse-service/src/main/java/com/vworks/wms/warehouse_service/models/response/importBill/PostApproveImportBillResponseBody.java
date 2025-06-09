package com.vworks.wms.warehouse_service.models.response.importBill;

import com.vworks.wms.warehouse_service.models.ApprovedDetailModel;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PostApproveImportBillResponseBody {
    private String id;
    private String code;
    private String approveDetail;
    private List<ApprovedDetailModel> approvedDetail;
    private String status;
}
