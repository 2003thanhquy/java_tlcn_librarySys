package com.spkt.librasys.constant;

import org.springframework.stereotype.Component;

@Component("roleProvider")
public class PredefinedRole {
    public static final String USER_ROLE = "USER";
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String MANAGER_ROLE = "MANAGER";
    private PredefinedRole() {}
}
