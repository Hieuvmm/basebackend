package com.vworks.wms.warehouse_service.models.request.order;

import com.vworks.wms.warehouse_service.models.MaterialOrderModel;
import com.vworks.wms.warehouse_service.utils.WarehouseServiceMessages;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseOrderReqBody {
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String customerCode;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String customerType;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String orderType;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String deliveryMethod;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String exchangeRateCode;
    private List<MaterialOrderModel> materialOrders;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String whExport;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String paidMethod;
    private String note;
    private String advanceAmount;
    private String advanceDate;
    private String tax;
    private String discountRate;
    private String status;
}
