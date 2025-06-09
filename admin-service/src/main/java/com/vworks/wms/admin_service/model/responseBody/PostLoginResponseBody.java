package com.vworks.wms.admin_service.model.responseBody;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostLoginResponseBody {
    private String accessToken;
    private Integer expiresIn;
    private String refreshToken;
    private Integer refreshExpiresIn;
    private String status;
}
