package store.juin.api.address.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.juin.api.address.model.entity.Address;
import store.juin.api.address.repository.querydsl.QuerydslAddressRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>, QuerydslAddressRepository {
    Optional<List<Address>> findAllByAccountId(Long id);

    Optional<Address> findByIdAndAccountId(Long addressId, Long accountId);
}
