package com.vworks.wms.warehouse_service.models.response.projectCategory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostListProjectCategoryResBody {
    private String code;
    private String name;
    private String description;
    private String status;
    private String createdDate;
}
