package com.vworks.wms.warehouse_service.models.request.unitType;

import com.vworks.wms.warehouse_service.utils.Commons;
import com.vworks.wms.warehouse_service.utils.WarehouseServiceMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BaseUnitTypeRequest {
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String code;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String name;
    private String description;
    @Pattern(regexp = Commons.STATUS_REGEXP, message = WarehouseServiceMessages.FIELD_FORMAT)
    private String status;
}
