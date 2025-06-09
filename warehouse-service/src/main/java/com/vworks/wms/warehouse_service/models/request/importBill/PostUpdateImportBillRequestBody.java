package com.vworks.wms.warehouse_service.models.request.importBill;

import com.vworks.wms.warehouse_service.models.request.importBill.detailImportMaterials.DetailImportMaterial;
import com.vworks.wms.warehouse_service.utils.WarehouseServiceMessages;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostUpdateImportBillRequestBody {
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String id;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String code;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String orderCode;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String importDate;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String orderDate;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String importContent;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String providerCode;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String warehouseCode;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String deliveryMethod;
    private List<DetailImportMaterial> detailImportMaterials;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String exchangeRateCode;
    private String description;
    private MultipartFile attachment;
    //    @Pattern(regexp = Commons.STATUS_REGEXP, message = WarehouseServiceMessages.FIELD_FORMAT)
    private String status;
}
