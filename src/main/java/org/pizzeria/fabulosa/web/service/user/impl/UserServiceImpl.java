package org.pizzeria.fabulosa.web.service.user.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.common.dao.user.UserRepository;
import org.pizzeria.fabulosa.common.entity.role.Role;
import org.pizzeria.fabulosa.common.entity.user.User;
import org.pizzeria.fabulosa.common.service.role.RoleService;
import org.pizzeria.fabulosa.web.dto.user.RegisterDTO;
import org.pizzeria.fabulosa.web.service.user.UserService;
import org.pizzeria.fabulosa.web.util.constant.ApiResponses;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.pizzeria.fabulosa.common.util.constant.AppConstants.DUMMY_ACCOUNT_EMAIL;

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
		Optional<Role> userRoleById = roleService.findByName("USER");

		if (userRoleById.isEmpty()) {
			throw new EntityNotFoundException("Role 'User' not found");
		}

		Set<Role> roles = new HashSet<>();
		roles.add(userRoleById.get());

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
		Optional<User> userById = userRepository.findById(userId);

		if (userById.isEmpty()) {
			throw new EntityNotFoundException(ApiResponses.USER_NOT_FOUND);
		}

		User user = userById.get();

		if (DUMMY_ACCOUNT_EMAIL.equals(user.getEmail())) {
			return true; // cannot delete dummy account
		}

		userRepository.deleteById(user.getId());
		return false; // no error otherwise
	}
}