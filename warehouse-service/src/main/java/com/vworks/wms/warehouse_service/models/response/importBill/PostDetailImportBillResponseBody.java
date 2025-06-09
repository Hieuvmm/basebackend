package com.vworks.wms.warehouse_service.models.response.importBill;

import com.vworks.wms.admin_service.entity.UserInfoEntity;
import com.vworks.wms.warehouse_service.models.ApprovedDetailModel;
import com.vworks.wms.warehouse_service.models.request.importBill.detailImportMaterials.DetailImportMaterial;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PostDetailImportBillResponseBody {
    private String id;
    private String code;
    private String orderCode;
    private String importDate;
    private String orderDate;
    private String importContent;
    private String providerCode;
    private String warehouseCode;
    private String deliveryMethod;
    private String exchangeRateCode;
    private String attachment;
    private String description;
    private BigDecimal totalPrice;
    private String approveDetail;
    private String followDetail;
    private List<ApprovedDetailModel> approvedDetail;
    private List<DetailImportMaterial> detailImportMaterials;
    private List<UserInfoEntity> approvals;
    private List<UserInfoEntity> follows;
    private String status;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String createdBy;
    private String updatedBy;
}
