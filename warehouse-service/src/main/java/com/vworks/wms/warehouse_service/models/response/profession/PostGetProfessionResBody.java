package com.vworks.wms.warehouse_service.models.response.profession;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PostGetProfessionResBody {
    private String code;
    private String name;
    private String status;
    private String description;
    private String createdDate;
    private String createdBy;
    private String updatedDate;
    private String updatedBy;
}
