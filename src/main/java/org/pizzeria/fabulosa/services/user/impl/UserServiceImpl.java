package org.pizzeria.fabulosa.services.user.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.entity.role.Role;
import org.pizzeria.fabulosa.entity.user.User;
import org.pizzeria.fabulosa.repos.user.UserRepository;
import org.pizzeria.fabulosa.services.role.RoleService;
import org.pizzeria.fabulosa.services.user.UserService;
import org.pizzeria.fabulosa.utils.Constants;
import org.pizzeria.fabulosa.web.dto.user.dto.RegisterDTO;
import org.pizzeria.fabulosa.web.dto.user.dto.UserDTO;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final RoleService roleService;

	private final PasswordEncoder bCryptEncoder;

	@Override
	public void createUser(RegisterDTO registerDTO) {
		String encodedPassword = bCryptEncoder.encode(registerDTO.password());
		Optional<Role> userRole = roleService.findByName("USER");
		Set<Role> roles = new HashSet<>();
		roles.add(userRole.get());

		User user = User.builder()
				.withName(registerDTO.name())
				.withEmail(registerDTO.email())
				.withContactNumber(registerDTO.contactNumber())
				.withPassword(encodedPassword)
				.withRoles(roles)
				.build();

		userRepository.save(user);
	}

	@Override
	public boolean deleteUserById(String password, Long userId) {
		User user = findUserById(userId);

		if (Constants.DUMMY_ACCOUNT_EMAIL.equals(user.getEmail())) {
			return true; // cannot delete dummy account
		}

		userRepository.deleteById(user.getId());
		return false; // no error otherwise
	}

	@Override
	public Optional<UserDTO> findUserDTOById(Long userId) {
		return userRepository.findUserById(userId);
	}

	@Override
	public User findUserReference(Long userId) {
		return userRepository.getReferenceById(userId);
	}

	// for internal use only

	public User findUserById(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException(String.format("UserNotFound %s", userId)));
	}

	@Override
	public void existsOrThrow(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new UsernameNotFoundException(String.format("UserNotFound %s", userId));
		}
	}
}