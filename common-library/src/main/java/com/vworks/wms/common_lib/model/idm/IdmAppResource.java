package com.vworks.wms.common_lib.model.idm;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdmAppResource {
    private String resourceId;
    private String name;
    private Set<String> uris;
    private String type;
    private Set<IdmAppScope> scopes;
    private String iconUri;
    private String displayName;
    private Map<String, List<String>> attributes;
}
