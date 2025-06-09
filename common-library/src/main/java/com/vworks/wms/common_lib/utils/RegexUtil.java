package com.vworks.wms.common_lib.utils;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class RegexUtil {
    public static final String PASSWORD = "^(?=.*[A-Z])(\\s*|(?=.*[!@#$%^&*()_+{}\\[\\]:;<>,.?~\\\\/\\-|])(?!\\s+)[a-zA-Z0-9!@#$%^&*()_+{}\\[\\]:;<>,.?~\\\\/\\-|]{6,})$";
    //    public static final String EMAIL = "^(\\s*|[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4})$";
//    public static final String PHONE = "^0[35789]\\d{8}$";
//    public static final String REGEX_BIRTHDAY = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$";
    public static final String REGEX_ACTION_UPDATE_PASS = "^(" + "\\s*" + "|" + "CHANGE" + "|" + "RESET" + ")$";
    public static final String REGEX_ROLE_TYPE = "^(" + "\\s*" + "|" + "DEFAULT" + "|" + "CUSTOM" + ")$";

    public static final String REGEX_STATUS_A_IN = "^(" + "\\s*" + "|" + "ACTIVE" + "|" + "INACTIVE" + ")$";
}
