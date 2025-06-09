package com.vworks.wms.admin_service.model.requestBody;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.keycloak.representations.userprofile.config.UPAttribute;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateUserAttributeRequest {
    List<UPAttribute> attributes;
}
