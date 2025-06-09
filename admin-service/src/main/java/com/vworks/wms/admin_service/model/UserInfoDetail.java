package com.vworks.wms.admin_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDetail {
    private String id;
    private String userId;
    private String userCode;
    private String username;
    private UserPersonalInfo userPersonalInfo;
    private UserJobInfo userJobInfo;
    private String roleCode;
    private Boolean changedPass;
    private String deviceId;
    private String status;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String createdBy;
    private String updatedBy;
}
