package com.vworks.wms.warehouse_service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetDetailProductModel {
    private String id;
    private List<GetDetailMaterialExModel> materialsEx;
    private String time;
}
