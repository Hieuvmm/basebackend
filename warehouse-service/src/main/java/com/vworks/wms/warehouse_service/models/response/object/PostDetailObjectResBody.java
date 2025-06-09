package com.vworks.wms.warehouse_service.models.response.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostDetailObjectResBody {
    private String code;
    private String name;
    private String type;
    private String phoneNumber;
    private Integer provinceCode;
    private Integer districtCode;
    private String addressDetail;
    private String professionCode;
    private String agentLevelCode;
    private String taxCode;
    private String maximumDebt;
    private String debtDay;
    private String bankName;
    private String bankNumber;
    private String businessManagerCode;
    private String description;
    private String status;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String createdBy;
    private String updatedBy;
}
