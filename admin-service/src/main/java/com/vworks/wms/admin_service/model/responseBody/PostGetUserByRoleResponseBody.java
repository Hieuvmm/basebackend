package com.vworks.wms.admin_service.model.responseBody;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostGetUserByRoleResponseBody {
    private String userId;
    private String username;
    private String fullName;
    private String userCode;
}
