package PizzaApp.api.services.address;

import PizzaApp.api.entity.address.Address;

import java.util.Optional;

public interface AddressService {

	Optional<Address> findByExample(Address address);

	Address findReference(Long addressId);

	Address findAddressById(Long addressId);
}
