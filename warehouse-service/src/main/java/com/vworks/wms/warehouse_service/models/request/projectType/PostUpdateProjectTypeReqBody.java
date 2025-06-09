package com.vworks.wms.warehouse_service.models.request.projectType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostUpdateProjectTypeReqBody {
    private String code;
    private String name;
    private String description;
    private String status;
}
