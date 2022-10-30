package com.ecommerce.backend.repository.querydsl.impl;

import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.repository.querydsl.ifs.QuerydslAddressRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.ecommerce.backend.domain.entity.QAddress.address;


@RequiredArgsConstructor
public class QuerydslAddressRepositoryImpl implements QuerydslAddressRepository {

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