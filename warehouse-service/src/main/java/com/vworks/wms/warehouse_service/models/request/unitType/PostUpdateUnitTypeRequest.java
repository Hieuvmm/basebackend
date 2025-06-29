package com.vworks.wms.warehouse_service.models.request.unitType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostUpdateUnitTypeRequest extends BaseUnitTypeRequest {
    private String id;
}
