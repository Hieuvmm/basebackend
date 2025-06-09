package com.vworks.wms.common_lib.utils;

public enum DateTimeFormatUtil {
    MM_YYYY("MM-yyyy"),
    YYYY_MM("yyyy-MM"),
    DD_MM_YYYY("dd-MM-yyyy"),
    YYYY_MM_DD("yyyy-MM-dd"),
    DD_MM_YYYY_1("dd/MM/yyyy"),
    YYYY_MM_DD_1("yyyy/MM/dd"),
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
    HH_MM_SS("HH:mm:ss"),
    HH_MM_SS_SSS("hh:mm:ss.SSS"),
    GMT_PLUS_7("GMT+7");

    private final String value;

    DateTimeFormatUtil(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
