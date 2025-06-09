package com.vworks.wms.warehouse_service.models.request.material;

import com.vworks.wms.common_lib.base.PaginationRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostListMaterialRequest extends PaginationRequest {
    private String status;
    private String whCode;
}
