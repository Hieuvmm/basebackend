package com.vworks.wms.warehouse_service.models.response.importBill;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PostChangeStatusImportBillResponseBody {
    private String id;
    private String code;
    private String name;
    private String description;
    private String status;
}
