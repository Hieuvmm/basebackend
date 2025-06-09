package com.vworks.wms.warehouse_service.models;

import lombok.*;

import java.math.BigDecimal;


@NoArgsConstructor
@Getter
@Setter
@Builder

public class DetailWareHouseModel {
    private String productCode;
    private String category;
    private String productName;
    private String unit;
    private Integer quantity;
    private BigDecimal price;
    private String ccy;
    private BigDecimal sellPrice;
    private String provider;
    private Long minInventory;
    private String status;

    public DetailWareHouseModel(String productCode, String category, String productName, String unit,
                                Integer quantity, BigDecimal price, String ccy, BigDecimal sellPrice,
                                String provider, Long minInventory, String status) {
        this.productCode = productCode;
        this.category = category;
        this.productName = productName;
        this.unit = unit;
        this.quantity = quantity;
        this.price = price;
        this.ccy = ccy;
        this.sellPrice = sellPrice;
        this.provider = provider;
        this.minInventory = minInventory;
        this.status = status;
    }

}



