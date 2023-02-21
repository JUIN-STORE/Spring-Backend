package store.juin.api.domain.request;

import lombok.Data;
import lombok.experimental.Accessors;

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
