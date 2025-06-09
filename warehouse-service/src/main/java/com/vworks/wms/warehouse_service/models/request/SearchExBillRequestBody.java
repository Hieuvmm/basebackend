package com.vworks.wms.warehouse_service.models.request;

import com.vworks.wms.common_lib.base.PaginationRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SearchExBillRequestBody extends PaginationRequest {
    private String billCode;
    private String status;
    private String pageSize;
    private String pageNumber;
}
