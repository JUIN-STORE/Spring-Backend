package store.juin.api.domain.request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthorizeRequest {
    private String fromEmail;
    private String toEmail;
    private String toName;
}
