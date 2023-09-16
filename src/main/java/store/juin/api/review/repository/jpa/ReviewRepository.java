package store.juin.api.review.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.juin.api.item.model.entity.Item;
import store.juin.api.review.model.entity.Review;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByReviewIdAndAccountId(Long reviewId, Long accountId);

    Page<Review> findAllByItem(Item item, Pageable pageable);
}
