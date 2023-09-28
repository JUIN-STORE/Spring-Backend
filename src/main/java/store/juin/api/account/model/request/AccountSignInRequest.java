package store.juin.api.account.model.request;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data @Accessors(chain = true)
public class AccountSignInRequest implements Serializable {
    private String identification;
    private String passwordHash;
}