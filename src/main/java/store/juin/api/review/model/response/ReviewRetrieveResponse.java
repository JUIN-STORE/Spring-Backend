package store.juin.api.review.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.review.model.entity.Review;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class ReviewRetrieveResponse {
    private Long reviewId;

    private Long accountId;

    private String accountName;

    private Integer rate;

    private String content;

    private LocalDateTime createdAt;

    public static ReviewRetrieveResponse from(Review review) {
        return new ReviewRetrieveResponse()
                .setReviewId(review.getReviewId())
                .setAccountId(review.getAccount().getId())
                .setAccountName(review.getAccount().getName())
                .setRate(review.getRate())
                .setContent(review.getContent())
                .setCreatedAt(review.getCreatedAt());
    }
}
