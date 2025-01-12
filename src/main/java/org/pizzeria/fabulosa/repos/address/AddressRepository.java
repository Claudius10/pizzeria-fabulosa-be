package org.pizzeria.fabulosa.repos.address;

import org.pizzeria.fabulosa.entity.address.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

	@Query("from Address address where address.id = :addressId")
	Optional<Address> findAddressById(Long addressId);
}