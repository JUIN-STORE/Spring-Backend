package store.juin.api.delivery.repository.querydsl.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import store.juin.api.delivery.model.entity.Delivery;
import store.juin.api.delivery.repository.querydsl.QuerydslDeliveryRepository;

import java.util.List;
import java.util.Optional;

import static store.juin.api.common.entity.QDelivery.delivery;


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
