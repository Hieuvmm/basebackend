package com.vworks.wms.common_lib.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayloadTokenInfo {
    private String userId;
    private String userCode;
    private String listGroupCode;
    private String listServiceId;
    private String listServiceCode;
    private String listApiId;
    private String listApiEndPoint;
    private String listApiCode;
    private Boolean changedPass;
    private String status;
    private String registeredDeviceId;
    private Integer expirationTime;
}
