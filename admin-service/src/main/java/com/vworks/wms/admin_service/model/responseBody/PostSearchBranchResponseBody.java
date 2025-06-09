package com.vworks.wms.admin_service.model.responseBody;

import com.vworks.wms.admin_service.model.DepartmentInfo;
import com.vworks.wms.common_lib.model.WarehouseInfo;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class PostSearchBranchResponseBody {
    private String id;
    private String code;
    private String name;
    private List<DepartmentInfo> departmentInfo;
    private List<WarehouseInfo> warehouseInfo;
    private String description;
    private String status;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String createdBy;
    private String updatedBy;
}
