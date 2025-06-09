package com.vworks.wms.warehouse_service.models.request.project;

import com.vworks.wms.common_lib.base.PaginationRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostListProjectReqBody extends PaginationRequest {
    private String status;
}
