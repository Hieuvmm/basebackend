package com.vworks.wms.warehouse_service.models.request.provider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostUpdateProviderRequest extends BaseProviderRequest {
    private String id;
}
