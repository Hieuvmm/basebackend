package com.vworks.wms.warehouse_service.models.response.warehouse;

import com.vworks.wms.warehouse_service.models.DetailWareHouseModel;
import lombok.Data;

import java.util.List;

@Data
public class PostGetDetailWareHouseResponseBody {
    private List<DetailWareHouseModel> productList;
}
