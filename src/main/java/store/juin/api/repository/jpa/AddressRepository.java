package store.juin.api.repository.jpa;

import store.juin.api.domain.entity.Address;
import store.juin.api.repository.querydsl.QuerydslAddressRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>, QuerydslAddressRepository {
    Optional<List<Address>> findAllByAccountId(Long id);

    Optional<Address> findByIdAndAccountId(Long addressId, Long accountId);
}
