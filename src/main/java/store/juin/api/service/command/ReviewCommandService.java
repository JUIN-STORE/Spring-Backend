package store.juin.api.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.juin.api.domain.entity.Account;
import store.juin.api.domain.entity.Item;
import store.juin.api.domain.entity.Review;
import store.juin.api.domain.request.ReviewRequest;
import store.juin.api.exception.Msg;
import store.juin.api.repository.jpa.ReviewRepository;
import store.juin.api.service.query.ItemQueryService;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class ReviewCommandService {
    private final ReviewRepository reviewRepository;
    private final OrderCommandService orderCommandService;
    private final ItemQueryService itemQueryService;

    @Transactional
    public long add(Account account, ReviewRequest.Add request) {
        final Long itemId = request.getItemId();

        final long count = orderCommandService.countByAccountIdAndItemId(account.getId(), itemId);
        if (count == 0) throw new EntityNotFoundException(Msg.ORDER_NOT_FOUND);

        final Item item = itemQueryService.readById(itemId);
        final Review review = reviewRepository.save(request.toReview(account, item));

        return review.getReviewId();
    }

    @Transactional
    public long modify(long accountId,
                       long reviewId,
                       ReviewRequest.Update request) {
        final Review review = reviewRepository.findByReviewIdAndAccountId(reviewId, accountId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.REVIEW_NOT_FOUND));

        review.updateReview(request.getRate(), request.getContent());
        return review.getReviewId();
    }

    @Transactional
    public void remove(long accountId, long reviewId) {
        final Review review = reviewRepository.findByReviewIdAndAccountId(reviewId, accountId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.REVIEW_NOT_FOUND));

        reviewRepository.delete(review);
    }
}
