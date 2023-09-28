package store.juin.api.account.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.token.model.response.TokenResponse;

@Data
@Accessors(chain = true)
public class AccountSignInResponse {

    private String identification;

    private TokenResponse token;

    public static AccountSignInResponse of(String identification, String accessToken) {
        return new AccountSignInResponse()
                .setIdentification(identification)
                .setToken(TokenResponse.of(accessToken));
    }
}
