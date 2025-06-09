package com.vworks.wms.common_lib.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarehouseInfo {
    private String id;
    private String code;
    private String name;
    private String phoneNumberWh;
    private String addressWh;
    private String managerWh;
    private String description;
    private String status;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String createdBy;
    private String updatedBy;
}
