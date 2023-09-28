package store.juin.api.review.model.request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ReviewUpdateRequest {
    private Integer rate;

    private String content;
}
