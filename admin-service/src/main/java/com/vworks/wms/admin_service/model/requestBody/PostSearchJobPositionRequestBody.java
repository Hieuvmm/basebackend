package com.vworks.wms.admin_service.model.requestBody;

import com.vworks.wms.common_lib.base.PaginationRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PostSearchJobPositionRequestBody extends PaginationRequest {
    private String status;
}
