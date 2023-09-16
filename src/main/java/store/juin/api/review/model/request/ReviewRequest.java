package store.juin.api.review.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import store.juin.api.account.model.entity.Account;
import store.juin.api.item.model.entity.Item;
import store.juin.api.review.model.entity.Review;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ReviewRequest {
    @Data
    @Accessors(chain = true)
    public static class Add {
        private Long itemId;

        private Integer rate;

        private String content;

        public Review toReview(Account account, Item item) {
            return Review.builder()
                    .account(account)
                    .item(item)
                    .rate(rate)
                    .content(content)
                    .build();
        }
    }

    @Data
    @Accessors(chain = true)
    public static class Update {
        private Integer rate;

        private String content;
    }
}
