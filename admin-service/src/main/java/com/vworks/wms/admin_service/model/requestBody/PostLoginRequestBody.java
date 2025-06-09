package com.vworks.wms.admin_service.model.requestBody;

import com.vworks.wms.admin_service.utils.ASMessages;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostLoginRequestBody {
    @NotBlank(message = ASMessages.TEMPLATE_REQUEST_INVALID)
    private String username;
    @NotBlank(message = ASMessages.TEMPLATE_REQUEST_INVALID)
    private String password;
}
