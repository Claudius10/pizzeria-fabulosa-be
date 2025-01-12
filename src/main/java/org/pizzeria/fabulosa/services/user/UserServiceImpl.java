package org.pizzeria.fabulosa.services.user;

import jakarta.transaction.Transactional;
import org.pizzeria.fabulosa.entity.address.Address;
import org.pizzeria.fabulosa.entity.role.Role;
import org.pizzeria.fabulosa.entity.user.User;
import org.pizzeria.fabulosa.repos.user.UserRepository;
import org.pizzeria.fabulosa.services.address.AddressService;
import org.pizzeria.fabulosa.services.role.RoleService;
import org.pizzeria.fabulosa.utils.Constants;
import org.pizzeria.fabulosa.web.dto.auth.RegisterDTO;
import org.pizzeria.fabulosa.web.dto.user.dto.UserDTO;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final RoleService roleService;

	private final AddressService addressService;

	private final PasswordEncoder bCryptEncoder;

	public UserServiceImpl(
			UserRepository userRepository,
			RoleService roleService,
			AddressService addressService,
			PasswordEncoder bCryptEncoder) {
		this.userRepository = userRepository;
		this.roleService = roleService;
		this.addressService = addressService;
		this.bCryptEncoder = bCryptEncoder;
	}

	@Override
	public void createUser(RegisterDTO registerDTO) {
		String encodedPassword = bCryptEncoder.encode(registerDTO.password());
		Optional<Role> userRole = roleService.findByName("USER");

		User user = new User.Builder()
				.withName(registerDTO.name())
				.withEmail(registerDTO.email())
				.withContactNumber(registerDTO.contactNumber())
				.withPassword(encodedPassword)
				.withRoles(userRole.get())
				.build();

		userRepository.save(user);
	}

	@Override
	public boolean addUserAddress(Long userId, Address address) {
		User user = findUserOrThrow(userId);

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
		User user = findUserOrThrow(userId);

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
	public void updateUserName(String password, Long userId, String name) {
		User user = findUserOrThrow(userId);
		user.setName(name);
	}

	@Override
	public void updateUserEmail(String password, Long userId, String email) {
		User user = findUserOrThrow(userId);
		user.setEmail(email);
	}

	@Override
	public void updateUserContactNumber(String password, Long userId, Integer contactNumber) {
		User user = findUserOrThrow(userId);
		user.setContactNumber(contactNumber);
	}

	@Override
	public void updateUserPassword(String password, Long userId, String newPassword) {
		String encodedPassword = bCryptEncoder.encode(newPassword);
		User user = findUserOrThrow(userId);
		user.setPassword(encodedPassword);
	}

	@Override
	public boolean deleteUserById(String password, Long userId) {
		User user = findUserOrThrow(userId);

		if (Constants.DUMMY_ACCOUNT_EMAIL.equals(user.getEmail())) {
			return true;
		}

		userRepository.deleteById(user.getId());
		return false;
	}

	@Override
	public Optional<UserDTO> findUserDTOById(Long userId) {
		return userRepository.findUserById(userId);
	}

	@Override
	public User findUserReference(Long userId) {
		return userRepository.getReferenceById(userId);
	}

	@Override
	public Set<Address> findUserAddressListById(Long userId) {
		return userRepository.findUserAddressListById(userId);
	}

	// for internal use only

	@Override
	public User findUserOrThrow(Long userId) {
		Optional<User> dbUser = userRepository.findById(userId);
		if (dbUser.isEmpty()) {
			throw new UsernameNotFoundException(String.format("UserNotFound %s", userId));
		} else {
			return dbUser.get();
		}
	}

	@Override
	public boolean existsById(Long userId) {
		return userRepository.existsById(userId);
	}
}