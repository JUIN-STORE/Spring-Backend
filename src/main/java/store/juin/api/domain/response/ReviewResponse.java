package store.juin.api.domain.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import store.juin.api.domain.entity.Review;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ReviewResponse {
    @Data
    @Accessors(chain = true)
    public static class Read {
        private Long reviewId;

        private String accountName;

        private Integer rate;

        private String content;

        public static Read from(Review review) {
            return new Read()
                    .setReviewId(review.getReviewId())
                    .setAccountName(review.getAccount().getName())
                    .setRate(review.getRate())
                    .setContent(review.getContent());
        }
    }
}
