package store.juin.api.common.enumeration;

import lombok.Getter;

import static store.juin.api.util.TimeUtil.ONE_MINUTE;

@Getter
public enum CacheType {
    AUTH_NUMBER(
            "AuthNumber",
            5 * ONE_MINUTE,
            10000
    );

    CacheType(String cacheName, long expireAfterWrite, long maximumSize) {
        this.cacheName = cacheName;
        this.expireAfterWrite = expireAfterWrite;
        this.maximumSize = maximumSize;
    }

    private final String cacheName;
    private final long expireAfterWrite;
    private final long maximumSize;

}
