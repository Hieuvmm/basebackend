package com.vworks.wms.warehouse_service.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ApprovedDetailModel {
    private String userId;
    private String userName;
    private String approveTime;
    private String status;
    private String note;
}
