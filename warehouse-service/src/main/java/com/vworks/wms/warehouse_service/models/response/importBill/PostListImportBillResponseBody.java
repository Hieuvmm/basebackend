package com.vworks.wms.warehouse_service.models.response.importBill;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PostListImportBillResponseBody {
    private String id;
    private String code;
    private String providerName;
    private String warehouseName;
    private BigDecimal totalBill;
    private String description;
    private Timestamp createdDate;
    private String createdBy;
    private String status;
}
