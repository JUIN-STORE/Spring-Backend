package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.jwt.TokenMessage;
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
