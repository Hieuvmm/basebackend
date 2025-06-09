package com.vworks.wms.warehouse_service.models.request.material;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscountMaterialModel {
    private String value;
    private String valueType;
    private String positionCode;
}
