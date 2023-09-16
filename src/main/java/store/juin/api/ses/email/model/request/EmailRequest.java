package store.juin.api.ses.email.model.request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class EmailRequest {
    private String toEmail;
    private String title;
    private String content;
}
