package com.vworks.wms.admin_service.model.requestBody;

import com.vworks.wms.common_lib.base.PaginationRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostSearchUserRequestBody extends PaginationRequest {
    private String status;
}
