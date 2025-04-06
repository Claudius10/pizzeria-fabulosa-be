package org.pizzeria.fabulosa.services.user;

import org.pizzeria.fabulosa.entity.address.Address;

import java.util.Set;

public interface UserAddressService {

	Set<Address> findUserAddressListById(Long userId);

	boolean addUserAddress(Long userId, Address address);

	boolean removeUserAddress(Long userId, Long addressId);
}
