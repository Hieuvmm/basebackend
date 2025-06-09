package com.vworks.wms.warehouse_service.models.request;

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
public class CategoryInProject {
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String status;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String projectCategoryCode;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private Integer projectCategoryQuantity;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String materialCode;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private Integer materialQuantity;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String startDate;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String endDate;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String technicianCode;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String note;
}
