package com.ecommerce.backend.repository.custom;

import com.ecommerce.backend.domain.entity.Address;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.ecommerce.backend.domain.entity.QAddress.address;


@RequiredArgsConstructor
public class CustomAddressRepositoryImpl implements CustomAddressRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Address> findByAccountIdAndDefaultAddress(Long accountId) {
        return Optional.ofNullable(
                queryFactory
                    .selectFrom(address)
                    .where(address.account.id.eq(accountId))
                    .where(address.defaultAddress.eq(true))
                    .fetchOne()
        );
    }
}