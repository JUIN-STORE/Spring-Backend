package store.juin.api.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import store.juin.api.domain.entity.Item;
import store.juin.api.domain.entity.Review;
import store.juin.api.domain.response.ReviewResponse;
import store.juin.api.handler.QueryTransactional;
import store.juin.api.repository.jpa.ReviewRepository;

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
