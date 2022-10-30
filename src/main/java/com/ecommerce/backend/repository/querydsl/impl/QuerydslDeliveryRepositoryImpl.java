package com.ecommerce.backend.repository.querydsl.impl;

import com.ecommerce.backend.domain.entity.Delivery;
import com.ecommerce.backend.repository.querydsl.ifs.QuerydslDeliveryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.ecommerce.backend.domain.entity.QDelivery.delivery;

@RequiredArgsConstructor
public class QuerydslDeliveryRepositoryImpl implements QuerydslDeliveryRepository {
    private final JPAQueryFactory queryFactory;

    @Transactional
    @Override
    public Long deleteByAddressIdIn(List<Long> addressIdList) {
        return queryFactory
                .delete(delivery)
                .where(delivery.address.id.in(addressIdList))
                .execute();
    }

    @Override
    public Optional<List<Delivery>> findByAddressIdIn(List<Long> addressIdList) {
        return Optional.ofNullable(
                queryFactory
                        .select(delivery)
                        .from(delivery)
                        .where(delivery.address.id.in(addressIdList))
                        .fetch()
        );
    }
}