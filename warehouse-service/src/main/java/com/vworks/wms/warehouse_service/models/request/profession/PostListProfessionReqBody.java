package com.vworks.wms.warehouse_service.models.request.profession;

import com.vworks.wms.common_lib.base.PaginationRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostListProfessionReqBody extends PaginationRequest {
    private String status;
}
