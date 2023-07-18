package store.juin.api.domain.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import store.juin.api.domain.entity.Account;
import store.juin.api.domain.entity.Item;
import store.juin.api.domain.entity.Review;

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
