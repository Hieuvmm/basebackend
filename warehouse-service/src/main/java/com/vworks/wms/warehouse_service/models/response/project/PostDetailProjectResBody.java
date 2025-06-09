package com.vworks.wms.warehouse_service.models.response.project;

import com.vworks.wms.warehouse_service.models.request.CategoryInProject;
import com.vworks.wms.warehouse_service.models.request.OtherInfoInProject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostDetailProjectResBody {
    private String code;
    private String name;
    private String projectTypeCode;
    private String customerCode;
    private String startDate;
    private String endDate;
    private Integer provinceCode;
    private Integer districtCode;
    private String addressDetail;
    private String supervisorCode;
    private String supervisorPhone;
    private String technicianCode;
    private String technicianPhone;
    private List<OtherInfoInProject> otherInfo;
    private List<CategoryInProject> categoryInfo;
    private String note;
    private String status;
    private String attachments;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String createdBy;
    private String updatedBy;
}
