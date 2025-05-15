package org.pizzeria.fabulosa.web.service.address;

import org.pizzeria.fabulosa.common.entity.address.Address;

import java.util.Optional;

public interface AddressService {

	Long create(Address address);

	Optional<Address> findByExample(Address address);

	Address findReference(Long addressId);

	Optional<Address> findAddressById(Long addressId);
}