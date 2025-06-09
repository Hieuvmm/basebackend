package com.vworks.wms.common_lib.service.impl;

import com.vworks.wms.common_lib.service.ServiceUtils;
import com.vworks.wms.common_lib.utils.Commons;
import com.vworks.wms.common_lib.utils.DateTimeFormatUtil;
import com.vworks.wms.common_lib.utils.MessageUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

@Component
@Slf4j
public class ServiceUtilsImpl implements ServiceUtils {
    @Override
    public String convertTimeStampToString(Timestamp date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateTimeFormatUtil.YYYY_MM_DD_HH_MM_SS.getValue());
        dateFormat.setTimeZone(TimeZone.getTimeZone(DateTimeFormatUtil.GMT_PLUS_7.getValue()));
        return dateFormat.format(date);
    }

    @Override
    public String getUserHeader(HttpServletRequest httpServletRequest) {
        return StringUtils.isBlank(httpServletRequest.getHeader(Commons.FIELD_USER_CODE)) ? httpServletRequest.getHeader(Commons.FIELD_USER_CODE) : null;
    }

    public LocalDate convertStringToLocalDate(String date) {
        List<String> dateTimeFormats = Arrays.asList(DateTimeFormatUtil.YYYY_MM_DD.getValue(),
                DateTimeFormatUtil.YYYY_MM_DD_1.getValue(),
                DateTimeFormatUtil.DD_MM_YYYY.getValue(),
                DateTimeFormatUtil.DD_MM_YYYY_1.getValue());

        for (String format : dateTimeFormats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                return LocalDate.parse(date, formatter);
            } catch (DateTimeParseException e) {
                // Continue to the next format
            }
        }

        // If none of the formats matched, throw an exception
        throw new IllegalArgumentException(MessageUtil.DATETIME_FORMAT_INVALID);
    }

    public String convertDateTimeStringToFormat(String date, String toFormatDate) {
        LocalDate localDate = this.convertStringToLocalDate(date);
        return localDate.format(DateTimeFormatter.ofPattern(toFormatDate));
    }

    public String convertLocalDateToDateString(LocalDate date, String formatDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatDate);
        return date.format(formatter);
    }

    public Integer compareLocalDateFromString(String date1, String date2) {
        LocalDate localDate1 = convertStringToLocalDate(date1);
        LocalDate localDate2 = convertStringToLocalDate(date2);
        return localDate1.compareTo(localDate2);
    }

    @Override
    public String convertTimeStampToStringWithFormatDate(Timestamp date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone(DateTimeFormatUtil.GMT_PLUS_7.getValue()));
        return dateFormat.format(date);
    }

    @Override
    public Timestamp convertStringToTimeStamp(String date) {
        List<String> dateTimeFormats = Arrays.asList(DateTimeFormatUtil.YYYY_MM_DD.getValue(),
                DateTimeFormatUtil.YYYY_MM_DD_1.getValue(),
                DateTimeFormatUtil.DD_MM_YYYY.getValue(),
                DateTimeFormatUtil.DD_MM_YYYY_1.getValue(),
                DateTimeFormatUtil.YYYY_MM_DD_HH_MM_SS.getValue());

        if (StringUtils.isEmpty(date)) return null;
        for (String format : dateTimeFormats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                LocalDate localDate = LocalDate.parse(date, formatter);
                return Timestamp.valueOf(localDate.atStartOfDay());
            } catch (Exception e) {
                // Nếu không thể parse với định dạng hiện tại, thử với định dạng tiếp theo
            }
        }

        return null;
    }

    public String convertTimeToString(Time date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateTimeFormatUtil.HH_MM_SS.getValue());
        dateFormat.setTimeZone(TimeZone.getTimeZone(DateTimeFormatUtil.GMT_PLUS_7.getValue()));
        return dateFormat.format(date);
    }

    public Time convertStringToTime(String date) throws ParseException {
        if (date == null || date.equals("")) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateTimeFormatUtil.HH_MM_SS.getValue());
        Date parsedDate = dateFormat.parse(date);
        return new Time(parsedDate.getTime());
    }

    @Override
    public String generateCodeFromName(String fullName) {
        String processedString = removeVietnameseSigns(fullName);
        StringBuilder result = new StringBuilder();
        List<String> wordsList = Arrays.asList(processedString.split("\\s+"));
        String name = wordsList.get(wordsList.size() - 1);
        result.append(name);
        for (int i = 0; i < wordsList.size() - 1; i++) {
            if (!wordsList.get(i).isEmpty()) {
                result.append(wordsList.get(i).charAt(0));
            }
        }
        return result.toString().toUpperCase();
    }

    public String removeVietnameseSigns(String str) {
        str = str.replace("Đ", "D");
        String nfdNormalizedString = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD);
        return nfdNormalizedString.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public boolean checkPattern(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(str).matches();
    }

    @Override
    public Pageable pageAble(String pageNumber, String pageSize) {
        int number = 0;
        int size = 10;

        if (!pageNumber.isEmpty() || !pageSize.isEmpty()) {
            if (!pageNumber.equalsIgnoreCase("0")) {
                number = Integer.parseInt(pageNumber);
            }
            if (!pageSize.equalsIgnoreCase("0")) {
                size = Integer.parseInt(pageSize);
            }
        }
        return PageRequest.of(number, size);
    }

    @Override
    public Pageable handlePageable(Integer page, Integer limit, Map<String, String> orders) {
        page = !Objects.isNull(page) ? page : 1;
        limit = !Objects.isNull(limit) ? limit : Integer.MAX_VALUE;
        if (Objects.nonNull(orders)) {
            Sort sort = Sort.by(orders.entrySet().stream()
                    .map(entry ->
                            new Sort.Order(Sort.Direction.DESC.name().equalsIgnoreCase(entry.getValue()) ? Sort.Direction.DESC : Sort.Direction.ASC, entry.getKey()))
                    .toList());
            return PageRequest.of(page - 1, limit, sort);
        }
        return PageRequest.of(page - 1, limit);
    }

    public Pageable pageAble(String pageNumber, String pageSize, String sortField, String sortBy) {
        int number = 0;
        int size = 10;

        if (!pageNumber.isEmpty() || !pageSize.isEmpty()) {
            if (!pageNumber.equalsIgnoreCase("0")) {
                number = Integer.parseInt(pageNumber);
            }
            if (!pageSize.equalsIgnoreCase("0")) {
                size = Integer.parseInt(pageSize);
            }
        }
        if (sortBy.trim().equalsIgnoreCase("desc")) {
            //Mặc định là tăng dần,còn có thêm .descending() là giảm dần
            return PageRequest.of(number, size, Sort.by(sortField).descending());
        }
        return PageRequest.of(number, size, Sort.by(sortField));
    }
}
