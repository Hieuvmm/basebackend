package com.vworks.wms.warehouse_service.models.request.object;

import com.vworks.wms.warehouse_service.utils.Commons;
import com.vworks.wms.warehouse_service.utils.WarehouseServiceMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostUpdateObjectReqBody {
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String code;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String name;
    @Pattern(regexp = Commons.OBJ_TYPE_REGEXP, message = WarehouseServiceMessages.FIELD_FORMAT)
    private String type;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String phoneNumber;
    @NotNull(message = WarehouseServiceMessages.REQUEST_INVALID)
    private Integer provinceCode;
    @NotNull(message = WarehouseServiceMessages.REQUEST_INVALID)
    private Integer districtCode;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String addressDetail;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String professionCode;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String agentLevelCode;
    //    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String taxCode;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String maximumDebt;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String debtDay;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String bankName;
    //    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String bankNumber;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String businessManagerCode;
    private String description;
    @Pattern(regexp = Commons.STATUS_REGEXP, message = WarehouseServiceMessages.FIELD_FORMAT)
    private String status;
    private String accountName;
}
