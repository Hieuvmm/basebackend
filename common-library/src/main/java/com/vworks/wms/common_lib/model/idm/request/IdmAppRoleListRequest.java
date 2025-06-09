package com.vworks.wms.common_lib.model.idm.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vworks.wms.common_lib.model.idm.IdmBaseSearchRequest;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdmAppRoleListRequest extends IdmBaseSearchRequest {
}
