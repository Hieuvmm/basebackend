package com.vworks.wms.common_lib.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.keycloak.representations.userprofile.config.UPAttribute;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdmUpdateUserAttributeRequest {
    private List<UPAttribute> attributes;
}
