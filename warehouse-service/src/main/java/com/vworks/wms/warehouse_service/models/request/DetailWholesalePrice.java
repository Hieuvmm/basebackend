package com.vworks.wms.warehouse_service.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DetailWholesalePrice {
    private BigDecimal value;
    private String valueType;
    private String positionCode;
}
