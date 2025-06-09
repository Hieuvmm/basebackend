package com.vworks.wms.common_lib.model.idm;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdmAppPermission {
    private String permissionId;
    private String name;
    private String type;
    private String description;
    private Object data;
}
