package com.vworks.wms.warehouse_service.models.response.material;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParameterModel {
    private String parameterTypeCode;
    private String parameterCode;
    private String parameterTypeName;
    private String parameterValue;
}
