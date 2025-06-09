package com.vworks.wms.warehouse_service.models.response.material;

import com.vworks.wms.warehouse_service.models.request.DetailWholesalePrice;
import com.vworks.wms.warehouse_service.models.request.ParametersMaterial;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PostDetailMaterialResponse {
    private String id;
    private String code;
    private String name;
    private String materialTypeCode;
    private String unitTypeCode;
    private BigDecimal listPrice;
    private BigDecimal sellPrice;
    private List<ParametersMaterial> parametersMaterials;
    private String origin;
    private Long minInventory;
    private List<DetailWholesalePrice> detailWholesalePrice;
    private List<String> image;
    private String description;
    private String status;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
}
