package com.vworks.wms.warehouse_service.models.response.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailWareHouseForExBillResponseBody {
    private String whCode;
    private String whName;
    List<DetailMaterialForExBillModel> detailMaterial;
}
