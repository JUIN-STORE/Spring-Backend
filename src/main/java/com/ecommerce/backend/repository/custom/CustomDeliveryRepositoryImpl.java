package com.ecommerce.backend.repository.custom;

import com.ecommerce.backend.domain.entity.Delivery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.ecommerce.backend.domain.entity.QDelivery.delivery;

@RequiredArgsConstructor
public class CustomDeliveryRepositoryImpl implements CustomDeliveryRepository {
    private final JPAQueryFactory queryFactory;

    @Transactional
    @Override
    public Long deleteByAddressIdList(List<Long> addressIdList) {
        return queryFactory
                .delete(delivery)
                .where(delivery.address.id.in(addressIdList))
                .execute();
    }

    @Override
    public Optional<List<Delivery>> findByAddressIdList(List<Long> addressIdList) {
        return Optional.ofNullable(
                queryFactory
                        .select(delivery)
                        .from(delivery)
                        .where(delivery.address.id.in(addressIdList))
                        .fetch()
        );
    }
}
