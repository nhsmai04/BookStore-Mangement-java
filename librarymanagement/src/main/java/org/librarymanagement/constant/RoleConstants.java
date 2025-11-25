package org.librarymanagement.constant;

import java.time.LocalDateTime;

public class RoleConstants {
    public static final int ADMIN = 1;
    public static final int USER = 2;
    public static final int MANAGER = 3;
    public static final LocalDateTime DATE_TIME = LocalDateTime.now();

    public static boolean isValid(Integer role) {
        return role != null &&
                (role == ADMIN || role == USER || role == MANAGER);
    }
}
