package com.vworks.wms.warehouse_service.models.request.projectType;

import com.vworks.wms.common_lib.base.PaginationRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostListProjectTypeReqBody extends PaginationRequest {
    private String status;
}
