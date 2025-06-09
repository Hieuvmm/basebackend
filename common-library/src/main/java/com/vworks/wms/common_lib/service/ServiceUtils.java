package com.vworks.wms.common_lib.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.Map;

public interface ServiceUtils {
    String generateCodeFromName(String fullName);

    Pageable pageAble(String pageNumber, String pageSize);

    Pageable handlePageable(Integer page, Integer limit, Map<String, String> orders);

    String convertTimeStampToString(Timestamp timestamp);

    String convertTimeStampToStringWithFormatDate(Timestamp date, String format);

    String getUserHeader(HttpServletRequest httpServletRequest);

    Timestamp convertStringToTimeStamp(String date);
}
