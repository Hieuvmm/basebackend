package com.vworks.wms.warehouse_service.models.request.profession;

import com.vworks.wms.warehouse_service.utils.Commons;
import com.vworks.wms.warehouse_service.utils.WarehouseServiceMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostCreateOrUpdateProfessionReqBody {
    private String code;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String name;
    @Pattern(regexp = Commons.STATUS_REGEXP, message = WarehouseServiceMessages.FIELD_FORMAT)
    private String status;
    private String description;
}
