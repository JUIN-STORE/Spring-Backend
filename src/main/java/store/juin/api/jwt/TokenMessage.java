package store.juin.api.jwt;

import static store.juin.api.utils.TimeUtil.ONE_DAY;
import static store.juin.api.utils.TimeUtil.ONE_HOUR;

public class TokenMessage {
    public static final long ACCESS_TOKEN_VALIDATION_TIME = 2 * ONE_HOUR;    // 2시간
    public static final long REFRESH_TOKEN_VALIDATION_TIME = 3 * ONE_DAY;    // 3일

    public static final String ACCESS_TOKEN = "Authorization";
    public static final String REFRESH_TOKEN = "Refresh-Token";
    public static final String BEARER = "Bearer ";
    public static final String EXCEPTION = "Exception";
}
