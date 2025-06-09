package com.vworks.wms.warehouse_service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchWareHouseModel {
    private String whCode;
    private String whName;
    private String whAddress;
    private String manager;
    private String phoneNumber;
    private String desc;
    private String status;
    private String userCodeManager;
}
