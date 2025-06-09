package com.vworks.wms.warehouse_service.models.request.exportBill;

import com.vworks.wms.warehouse_service.models.ProductExModel;
import lombok.Data;

import java.util.List;

@Data
public class CreateExportBillRequestBody {
    //    private String typeEx;
    private String orderNumber;
    private String destination;
    private String exCode;
    private String dateBill;
    private String customer;
    private String dateEx;
    private String desc;
    private String whCode;
    private List<ProductExModel> productEx;
    private String totalMoney;
    private String ccy;
    private String totalPriceBC;
    private List<String> approvalBy;
    private List<String> followBy;

}
