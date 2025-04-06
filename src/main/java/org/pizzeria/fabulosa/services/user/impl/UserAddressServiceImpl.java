package org.pizzeria.fabulosa.services.user.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.entity.address.Address;
import org.pizzeria.fabulosa.entity.user.User;
import org.pizzeria.fabulosa.repos.user.UserRepository;
import org.pizzeria.fabulosa.services.address.AddressService;
import org.pizzeria.fabulosa.services.user.UserAddressService;
import org.pizzeria.fabulosa.services.user.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAddressServiceImpl implements UserAddressService {

	private final UserRepository userRepository;

	private final UserService userService;

	private final AddressService addressService;

	@Override
	public boolean addUserAddress(Long userId, Address address) {
		User user = userService.findUserById(userId);
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
		User user = userService.findUserById(userId);

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
		return userRepository.findUserAddressListById(userId);
	}
}
