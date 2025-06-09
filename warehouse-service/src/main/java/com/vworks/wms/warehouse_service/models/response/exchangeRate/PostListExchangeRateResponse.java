package com.vworks.wms.warehouse_service.models.response.exchangeRate;


import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PostListExchangeRateResponse {
    private String id;
    private String code;
    private String name;
    private BigDecimal value;
    private String exchangeType;
    private String status;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;

}
