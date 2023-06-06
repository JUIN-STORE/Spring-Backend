package store.juin.api.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.juin.api.domain.entity.Item;
import store.juin.api.domain.entity.Review;
import store.juin.api.domain.response.ReviewResponse;
import store.juin.api.repository.jpa.ReviewRepository;

@Service
@RequiredArgsConstructor
public class ReviewQueryService {
    private final ReviewRepository reviewRepository;
    private final ItemQueryService itemQueryService;

    @Transactional(readOnly = true)
    public Page<ReviewResponse.Read> readAll(long itemId, Pageable pageable) {
        final Item item = itemQueryService.readById(itemId);
        final Page<Review> reviewList = reviewRepository.findAllByItem(item, pageable);
        return reviewList.map(ReviewResponse.Read::from);
    }
}
