package store.juin.api.review.model.request;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.account.model.entity.Account;
import store.juin.api.item.model.entity.Item;
import store.juin.api.review.model.entity.Review;

@Data
@Accessors(chain = true)
public class ReviewAddRequest {
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