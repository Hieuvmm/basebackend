package com.vworks.wms.warehouse_service.models.request.exchangeRate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostUpdateExchangeRateRequest extends BaseExchangeRateRequest {
    private String id;
}
