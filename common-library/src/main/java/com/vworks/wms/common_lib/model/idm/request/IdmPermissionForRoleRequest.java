package com.vworks.wms.common_lib.model.idm.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdmPermissionForRoleRequest {
    @NotEmpty
    private String realm;
    @NotEmpty
    private String clientId;
    private String roleId;
}
