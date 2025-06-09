package com.vworks.wms.admin_service.config;

public class AsConstant {

    public enum RequestMapping {;
        public static final String AS_AUTH =  "/wms/as/v1/auth";
        public static final String AS_ORG_BRANCH = "/wms/as/v1/org/branch";
        public static final String AS_ORG_DEPARTMENT = "/wms/as/v1/org/department";
        public static final String AS_USER_POSITION = "/wms/as/v1/user/position";
        public static final String AS_USER_TITLE = "/wms/as/v1/user/title";
        public static final String AS_PERMISSION = "/wms/as/v1/permission";
        public static final String AS_ROLE = "/wms/as/v1/role";
        public static final String AS_USER = "/wms/as/v1/user";
    }

    public enum Endpoint {;
        public static final String LOGIN = "/login";
        public static final String LOGOUT = "/logout";
        public static final String REFRESH = "/refresh";
    }
}
