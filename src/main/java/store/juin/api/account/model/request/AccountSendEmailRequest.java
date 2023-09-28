package store.juin.api.account.model.request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AccountSendEmailRequest {
    private String identification;
    private String email;
}