package com.vworks.wms.warehouse_service.models.request.exportBill;

import com.vworks.wms.warehouse_service.models.ProductExModel;
import lombok.Data;

import java.util.List;

@Data
public class PostUpdateExportBillRequestBody {
    //    private String typeEx;//kiểu của phiếu xuất
    private String orderNumber;//Mã hoá đơn
    private String destination;//xuất đến kho nào
    private String exCode;//mã phiếu xuất
    private String dateBill;//Ngày tạo hoá đơn
    private String customer;//khách hàng
    private String dateEx;//Ngày xuất hoá đơn
    private String desc;//Mô tả
    private String whCode;//Mã kho
    private List<ProductExModel> productEx;
    private String totalMoney;//Tổng hoá đơn
    private String ccy;//ĐƠn vị tiền tệ
    private String totalPriceBC;//Chữ
    private List<String> approvalBy;//Người phê duyệt
    private List<String> followBy;//Người theo dõi
    private String status;
}
