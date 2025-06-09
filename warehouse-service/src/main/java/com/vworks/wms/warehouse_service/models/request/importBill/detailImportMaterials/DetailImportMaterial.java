package com.vworks.wms.warehouse_service.models.request.importBill.detailImportMaterials;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DetailImportMaterial {
    private String materialTypeCode;
    private String materialTypeName;
    private String materialCode;
    private String materialName;
    private String unitTypeCode;
    private String unitTypeName;
    //    private String parameter;
    private int expectedQuantity;
    private int realQuantity;
    private BigDecimal price;
}
