package com.vworks.wms.warehouse_service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetDetailMaterialExModel {
    private String materialType;//loại sản phẩm/vật tư
    private String materialCode;// Mã vật tư
    private String materialName; //Tên vật tư
    //    private String parameter;//Thông số
    private String unit;// Đơn vị tính
    private String expQuantity;//Số lượng chứng từ
    private String realQuantity;//số lượng thực nhập
    private String price;//Giá thành
    private String totalPrice;//Tổng giá
}
