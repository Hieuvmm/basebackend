package com.vworks.wms.warehouse_service.models.request.project;

import com.vworks.wms.warehouse_service.models.request.CategoryInProject;
import com.vworks.wms.warehouse_service.models.request.OtherInfoInProject;
import com.vworks.wms.warehouse_service.utils.WarehouseServiceMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class PostUpdateProjectReqBody {
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String code;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String name;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String projectTypeCode;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String customerCode;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String startDate;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String endDate;
    @NotNull(message = WarehouseServiceMessages.REQUEST_INVALID)
    private Integer provinceCode;
    @NotNull(message = WarehouseServiceMessages.REQUEST_INVALID)
    private Integer districtCode;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String addressDetail;
    private String supervisorCode;
    private String supervisorPhone;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String technicianCode;
    @NotBlank(message = WarehouseServiceMessages.REQUEST_INVALID)
    private String technicianPhone;
    private List<OtherInfoInProject> otherInfo;
    private List<CategoryInProject> categoryInfo;
    private String note;
    private MultipartFile attachments;
}
