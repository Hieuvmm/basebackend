package com.vworks.wms.warehouse_service.models.request.material;

import lombok.Data;

import java.util.List;

@Data
public class PostDetailMaterialListRequestBody {
    private List<String> codeList;
}
