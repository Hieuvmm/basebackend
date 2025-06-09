package com.vworks.wms.warehouse_service.models;

import com.vworks.wms.warehouse_service.models.request.material.DiscountMaterialModel;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialOrderModel {
    private String code;
    private String name;
    private int quantity;
    private BigDecimal price;
    private String unit;
    private String priceDiscount;
    private String materialType;
    private String parameter;
    private DiscountMaterialModel discountMaterialModel;
}
