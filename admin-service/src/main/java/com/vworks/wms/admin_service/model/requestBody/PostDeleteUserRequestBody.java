package com.vworks.wms.admin_service.model.requestBody;

import lombok.Data;

@Data
public class PostDeleteUserRequestBody {
    private String userId;
    private String userCode;
    private String userName;
}
