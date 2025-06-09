package com.vworks.wms.warehouse_service.models;

import lombok.Data;

@Data
public class MaterialsExModel {
    private String materialCode;//mã vật tư
    private String expQuantity;//số lượng theo chứng từ
    private String realQuantity;//Số lượng thực tế
    private String totalPrice;//Thành tiền
    private String unit;//
}
