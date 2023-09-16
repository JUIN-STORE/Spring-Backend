package store.juin.api.token.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.token.jwt.TokenMessage;

@Data @Accessors(chain = true)
public class TokenResponse {
    private String accessToken;

    public static TokenResponse of(String accessToken) {
        return new TokenResponse()
                .setAccessToken(TokenMessage.BEARER + accessToken);
    }
}
