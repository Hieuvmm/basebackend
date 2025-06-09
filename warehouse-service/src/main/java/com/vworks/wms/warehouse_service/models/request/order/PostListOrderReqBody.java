package com.vworks.wms.warehouse_service.models.request.order;

import com.vworks.wms.common_lib.base.PaginationRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostListOrderReqBody extends PaginationRequest {
    private String status;
}
