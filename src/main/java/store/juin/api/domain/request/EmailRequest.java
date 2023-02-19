package store.juin.api.domain.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailRequest {
    private String toEmail;
    private String title;
    private String content;
}
