package store.juin.api.ses.authorize.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AuthorizeRequest {
    @Data
    @Accessors(chain = true)
    public static class Send {
        private String toEmail;
    }

    @Data
    @Accessors(chain = true)
    public static class Check {
        private String email;
        private String hash;
    }
}
