package store.juin.api.review.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import store.juin.api.review.model.entity.Review;

import java.time.LocalDateTime;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ReviewResponse {
    @Data
    @Accessors(chain = true)
    public static class Read {
        private Long reviewId;

        private Long accountId;

        private String accountName;

        private Integer rate;

        private String content;

        private LocalDateTime createdAt;

        public static Read from(Review review) {
            return new Read()
                    .setReviewId(review.getReviewId())
                    .setAccountId(review.getAccount().getId())
                    .setAccountName(review.getAccount().getName())
                    .setRate(review.getRate())
                    .setContent(review.getContent())
                    .setCreatedAt(review.getCreatedAt());
        }
    }
}
