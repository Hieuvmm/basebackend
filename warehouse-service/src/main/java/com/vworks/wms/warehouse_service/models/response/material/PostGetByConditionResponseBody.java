package com.vworks.wms.warehouse_service.models.response.material;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PostGetByConditionResponseBody {
    private String id;
    private String code;
    private String name;
    private String materialTypeName;
    private String unitTypeName;
    private String materialTypeCode;
    private String unitTypeCode;
    //    private String parameter;
    private BigDecimal price;
}
