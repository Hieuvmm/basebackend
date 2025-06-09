package com.vworks.wms.warehouse_service.models.request.warehouse;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostFetchWarehouseDetailRequestBody {
    private List<String> codes;
}
