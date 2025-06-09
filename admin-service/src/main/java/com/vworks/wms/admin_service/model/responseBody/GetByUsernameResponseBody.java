package com.vworks.wms.admin_service.model.responseBody;

import lombok.Data;

@Data
public class GetByUsernameResponseBody {
    private String userId;
    private String userCode;
    private String userName;
    private String fullName;
}
