package store.juin.api.review.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import store.juin.api.common.handler.QueryTransactional;
import store.juin.api.item.model.entity.Item;
import store.juin.api.item.service.query.ItemQueryService;
import store.juin.api.review.model.entity.Review;
import store.juin.api.review.model.response.ReviewResponse;
import store.juin.api.review.repository.jpa.ReviewRepository;

@Service
@RequiredArgsConstructor
public class ReviewQueryService {
    private final QueryTransactional queryTransactional;

    private final ReviewRepository reviewRepository;

    private final ItemQueryService itemQueryService;

    public Page<ReviewResponse.Read> readAll(long itemId, Pageable pageable) {
        return queryTransactional.execute(() -> {
            final Item item = itemQueryService.readById(itemId);
            final Page<Review> reviewList = reviewRepository.findAllByItem(item, pageable);
            return reviewList.map(ReviewResponse.Read::from);
        });
    }
}
