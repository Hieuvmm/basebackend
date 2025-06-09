package com.vworks.wms.warehouse_service.models.request.material;

import com.vworks.wms.warehouse_service.utils.WarehouseServiceMessages;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostDetailMaterialRequest {
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String code;
}
