package com.ecommerce.backend.jwt;

public class TokenMessage {
    private static final long ONE_SECOND = 1000L;
    private static final long ONE_MINUTE = 60 * ONE_SECOND;
    private static final long ONE_HOUR = 60 * ONE_MINUTE;
    private static final long ONE_DAY = 24 * ONE_HOUR;

    public static final long ACCESS_TOKEN_VALIDATION_TIME = 2 * ONE_HOUR;    // 2시간
    public static final long REFRESH_TOKEN_VALIDATION_TIME = 3 * ONE_DAY;    // 3일

    public static final String ACCESS_TOKEN = "Authorization";
    public static final String REFRESH_TOKEN = "Refresh-Token";
    public static final String BEARER = "Bearer ";
    public static final String EXCEPTION = "Exception";
}
