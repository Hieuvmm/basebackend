package com.vworks.wms.admin_service.model;

import lombok.Data;

import java.sql.Date;

@Data
public class UserPersonalInfo {
    private String fullName;
    private Date birthday;
    private String gender;
    private String phone;
    private String email;
    private String personalEmail;
    private String address;
    private String avatar;
}
