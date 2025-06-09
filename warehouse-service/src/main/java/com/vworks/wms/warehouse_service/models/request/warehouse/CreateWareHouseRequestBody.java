package com.vworks.wms.warehouse_service.models.request.warehouse;

import lombok.Data;

@Data
public class CreateWareHouseRequestBody {
    private String whCode;
    private String whName;
    private String userName;
    private String phoneNumber;
    private String whAddress;
    private String whDesc;
    private String status;
}
