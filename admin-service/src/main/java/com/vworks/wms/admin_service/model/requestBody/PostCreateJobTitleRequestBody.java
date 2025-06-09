package com.vworks.wms.admin_service.model.requestBody;

import lombok.Data;

@Data
public class PostCreateJobTitleRequestBody {
    private String code;
    private String name;
    private String status;
    private String desc;
}
