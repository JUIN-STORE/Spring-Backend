package store.juin.api.repository.querydsl.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import store.juin.api.domain.entity.Address;
import store.juin.api.domain.request.AddressRequest;
import store.juin.api.repository.querydsl.QuerydslAddressRepository;

import java.util.List;
import java.util.Optional;

import static store.juin.api.domain.entity.QAddress.address;


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
    public Address findByAccountIdAndZipCodeAndCityAndStreet(Long accountId, AddressRequest.Create create) {
        return
                queryFactory
                        .selectFrom(address)
                        .where(address.account.id.eq(accountId))
                        .where(address.zipCode.eq(create.getZipCode()))
                        .where(address.city.eq(create.getCity()))
                        .where(address.street.eq(create.getStreet()))
                        .fetchOne();
    }

    @Transactional
    @Override
    public long delete(Long accountId, Long addressId) {
        return
                queryFactory
                        .delete(address)
                        .where(address.account.id.eq(accountId))
                        .where(address.id.eq(addressId))
                        .execute();
    }

    @Transactional
    @Override
    public long deleteByAddressIdList(Long accountId, List<Long> addressIdList) {
        return
                queryFactory
                .delete(address)
                .where(address.account.id.eq(accountId))
                .where(address.id.in(addressIdList))
                .execute();
    }
}