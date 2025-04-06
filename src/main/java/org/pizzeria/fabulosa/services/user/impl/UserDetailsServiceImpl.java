package org.pizzeria.fabulosa.services.user.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.entity.user.User;
import org.pizzeria.fabulosa.services.user.UserDetailsService;
import org.pizzeria.fabulosa.services.user.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserService userService;

	private final PasswordEncoder bCryptEncoder;

	@Override
	public void updateUserName(String password, Long userId, String name) {
		User user = userService.findUserById(userId);
		user.setName(name);
	}

	@Override
	public void updateUserEmail(String password, Long userId, String email) {
		User user = userService.findUserById(userId);
		user.setEmail(email);
	}

	@Override
	public void updateUserContactNumber(String password, Long userId, Integer contactNumber) {
		User user = userService.findUserById(userId);
		user.setContactNumber(contactNumber);
	}

	@Override
	public void updateUserPassword(String password, Long userId, String newPassword) {
		User user = userService.findUserById(userId);
		String encodedPassword = bCryptEncoder.encode(newPassword);
		user.setPassword(encodedPassword);
	}
}
