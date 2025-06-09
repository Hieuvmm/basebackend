package com.vworks.wms.warehouse_service.models.response.order;

import com.vworks.wms.warehouse_service.entities.OrderEntity;
import com.vworks.wms.warehouse_service.models.MaterialOrderModel;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDetailOrderResBody {
    private OrderEntity order;
    private List<MaterialOrderModel> materialOrders;
}
