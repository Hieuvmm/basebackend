package com.vworks.wms.common_lib.model.idm;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdmBaseSearchRequest implements Serializable {
    @NotEmpty
    private String realm;
    @NotEmpty
    private String clientId;
    private String key;
    private Integer first;
    private Integer max;
}
