package com.vworks.wms.admin_service.model.requestBody;

import com.vworks.wms.admin_service.utils.ASMessages;
import com.vworks.wms.common_lib.utils.RegexUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PostCreateRoleRequestBody {
    @NotBlank(message = ASMessages.TEMPLATE_REQUEST_INVALID)
    private String code;
    @NotBlank(message = ASMessages.TEMPLATE_REQUEST_INVALID)
    private String name;
    @NotBlank(message = ASMessages.TEMPLATE_REQUEST_INVALID)
    @Pattern(regexp = RegexUtil.REGEX_ROLE_TYPE, message = ASMessages.FIELD_INVALID)
    private String type;
    private Object scope;
    private Object applicable;
    private Object resourceAccess;
    @NotBlank(message = ASMessages.TEMPLATE_REQUEST_INVALID)
    private String description;
    @NotBlank(message = ASMessages.TEMPLATE_REQUEST_INVALID)
    @Pattern(regexp = RegexUtil.REGEX_STATUS_A_IN, message = ASMessages.FIELD_INVALID)
    private String status;
}
