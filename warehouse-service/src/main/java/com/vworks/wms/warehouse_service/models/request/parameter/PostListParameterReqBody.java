package com.vworks.wms.warehouse_service.models.request.parameter;

import com.vworks.wms.common_lib.base.PaginationRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostListParameterReqBody extends PaginationRequest {
    private String status;
    private String prTypeCode;
}
