package com.vworks.wms.warehouse_service.models.response.importBill;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PostUpdateImportBillResponseBody {
    private String id;
    private String status;
    private String code;
    private String warehouseName;
    private Timestamp createdDate;
    private String providerName;
    private String createdBy;
    private String description;
    private BigDecimal totalBill;
}
