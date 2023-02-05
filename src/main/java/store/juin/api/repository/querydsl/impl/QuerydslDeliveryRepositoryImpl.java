package store.juin.api.repository.querydsl.impl;

import store.juin.api.domain.entity.Delivery;
import store.juin.api.repository.querydsl.QuerydslDeliveryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static store.juin.api.domain.entity.QDelivery.delivery;


@RequiredArgsConstructor
public class QuerydslDeliveryRepositoryImpl implements QuerydslDeliveryRepository {
    private final JPAQueryFactory queryFactory;

    @Transactional
    @Override
    public long deleteByAddressIdIn(List<Long> addressIdList) {
        return queryFactory
                .delete(delivery)
                .where(delivery.deliveryAddress.id.in(addressIdList))
                .execute();
    }

    @Override
    public Optional<List<Delivery>> findAllByAddressIdIn(List<Long> addressIdList) {
        return Optional.ofNullable(
                queryFactory
                        .select(delivery)
                        .from(delivery)
                        .where(delivery.deliveryAddress.id.in(addressIdList))
                        .fetch()
        );
    }
}
