package store.juin.api.ses.authorize.model.request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthorizeCheckRequest {
    private String email;

    private String hash;
}
