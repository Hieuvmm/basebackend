package com.vworks.wms.warehouse_service.models;

import lombok.Data;

import java.util.List;

@Data
public class ProductExModel {
    private List<MaterialsExModel> materialsEx;
    private String time;
}
