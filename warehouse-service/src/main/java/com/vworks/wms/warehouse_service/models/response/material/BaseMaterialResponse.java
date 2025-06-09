package com.vworks.wms.warehouse_service.models.response.material;

import com.vworks.wms.warehouse_service.models.request.material.DiscountMaterialModel;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BaseMaterialResponse {
    private String id;
    private String code;
    private String name;
    private String description;
    private String listPrice;
    private String status;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private String unit;
    private DiscountMaterialModel discountMaterialModel;
    private String materialType;
    private List<ParameterModel> parameterModels;
}
