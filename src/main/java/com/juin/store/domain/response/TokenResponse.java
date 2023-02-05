package com.juin.store.domain.response;

import com.juin.store.jwt.TokenMessage;
import lombok.Data;
import lombok.experimental.Accessors;

@Data @Accessors(chain = true)
public class TokenResponse {
    private String accessToken;

    public static TokenResponse of(String accessToken) {
        return new TokenResponse()
                .setAccessToken(TokenMessage.BEARER + accessToken);
    }
}
