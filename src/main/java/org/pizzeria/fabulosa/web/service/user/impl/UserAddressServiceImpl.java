package org.pizzeria.fabulosa.web.service.user.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.common.dao.user.UserAddressRepository;
import org.pizzeria.fabulosa.common.entity.address.Address;
import org.pizzeria.fabulosa.common.entity.user.User;
import org.pizzeria.fabulosa.web.service.address.AddressService;
import org.pizzeria.fabulosa.web.service.internal.UserServiceInternal;
import org.pizzeria.fabulosa.web.service.user.UserAddressService;
import org.pizzeria.fabulosa.web.util.constant.ApiResponses;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAddressServiceImpl implements UserAddressService {

	private final UserAddressRepository userAddressRepository;

	private final UserServiceInternal userServiceInternal;

	private final AddressService addressService;

	@Override
	public boolean addUserAddress(Long userId, Address address) {

		Optional<User> userById = userServiceInternal.findUserById(userId);

		if (userById.isEmpty()) {
			throw new EntityNotFoundException(ApiResponses.USER_NOT_FOUND);
		}

		User user = userById.get();

		if (user.getAddressList().size() == 3) {
			return false;
		}

		Optional<Address> dbAddress = addressService.findByExample(address);

		if (dbAddress.isPresent()) {
			user.addAddress(dbAddress.get());
		} else {
			user.addAddress(address);
		}

		return true;
	}

	@Override
	public boolean removeUserAddress(Long userId, Long addressId) {
		Optional<User> userById = userServiceInternal.findUserById(userId);

		if (userById.isEmpty()) {
			throw new EntityNotFoundException(ApiResponses.USER_NOT_FOUND);
		}

		User user = userById.get();

		Optional<Address> dbAddress = user.getAddressList()
				.stream()
				.filter(address1 -> address1.getId().equals(addressId))
				.findFirst();

		if (dbAddress.isEmpty()) {
			return false;
		}

		user.removeAddress(dbAddress.get());
		return true;
	}

	@Override
	public Set<Address> findUserAddressListById(Long userId) {
		return userAddressRepository.findUserAddressListById(userId);
	}
}
