package com.ecommerce.backend.repository.querydsl;

import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.request.AddressRequest;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
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

    @Override
    public Address findByAccountIdAndZipCodeAndCityAndStreet(Long accountId, AddressRequest.Register register) {
        return
                queryFactory
                        .selectFrom(address)
                        .where(address.account.id.eq(accountId))
                        .where(address.zipCode.eq(register.getZipCode()))
                        .where(address.city.eq(register.getCity()))
                        .where(address.street.eq(register.getStreet()))
                        .fetchOne();
    }

    @Override
    public long removeByAddressIdList(List<Long> addressIdList) {
        return
                queryFactory
                .delete(address)
                .where(address.id.in(addressIdList))
                .execute();
    }
}