package com.vworks.wms.warehouse_service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchExBillModel {
    private String exCode;
    private String name;
    private String statusPayment;
    private String customer;
    private String wareHouse;
    private String provider;
    private String totalPrice;
    private String status;
    private String createdBy;
    private String createdDate;
}
