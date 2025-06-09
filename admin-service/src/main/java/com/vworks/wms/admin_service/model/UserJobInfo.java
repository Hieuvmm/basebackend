package com.vworks.wms.admin_service.model;

import lombok.Data;

import java.sql.Date;

@Data
public class UserJobInfo {
    private String jobAttendanceCode;
    private String jobManager;
    private String jobPositionCode;
    private String jobTitleCode;
    private String jobDepartmentCode;
    private String jobAddress;
    private Date jobOnboardDate;
    private Date jobOfficialDate;
}
