package com.vworks.wms.warehouse_service.models.response.exportBill;

import com.vworks.wms.warehouse_service.models.GetDetailProductModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostGetExportBillDetailResponseBody {
    private String typeEx;
    private String orderNumber;
    private String exCode;
    private String destination;
    private String dateBill;
    private String customer;
    private String dateEx;
    private String desc;
    private String whName;
    private String whCode;
    private List<GetDetailProductModel> productEx;
    private String totalMoney;
    private String ccy;
    private String totalPriceBC;
    private List<String> approvalBy;
    private List<String> followBy;
    private String status;
}
