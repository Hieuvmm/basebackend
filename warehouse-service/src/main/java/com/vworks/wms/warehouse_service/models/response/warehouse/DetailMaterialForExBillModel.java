package com.vworks.wms.warehouse_service.models.response.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailMaterialForExBillModel {
    private String materialCode;
    private String materialName;
    private String materialType;
    private String unit;
    private String price;
    //    private String param;
    private Integer quantity;
}
