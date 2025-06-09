package com.vworks.wms.warehouse_service.models.response.object;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
public class PostListObjectResBody {
    private String code;
    private String name;
    private String type;
    private String agentLevelCode;
    private String phoneNumber;
    private Integer provinceCode;
    private Integer districtCode;
    private String addressDetail;
    private String status;
    private String createdDate;
}
