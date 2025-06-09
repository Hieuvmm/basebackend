package com.vworks.wms.warehouse_service.models.request.unitType;

import com.vworks.wms.common_lib.base.PaginationRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostListUnitTypeRequest extends PaginationRequest {
    private String status;
}
