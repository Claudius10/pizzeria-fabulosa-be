package org.pizzeria.fabulosa.web.service.internal.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.pizzeria.fabulosa.common.dao.internal.UserRepositoryInternal;
import org.pizzeria.fabulosa.common.entity.user.User;
import org.pizzeria.fabulosa.web.service.internal.UserServiceInternal;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
@Transactional
public class UserServiceInternalImpl implements UserServiceInternal {

	private final UserRepositoryInternal userRepositoryInternal;

	@Override
	public Optional<User> findUserById(Long userId) {
		return userRepositoryInternal.findById(userId);
	}

	@Override
	public Optional<User> findUserByEmail(String email) {
		return userRepositoryInternal.findUserByEmail(email);
	}

	@Override
	public User findReference(Long userId) {
		return userRepositoryInternal.getReferenceById(userId);
	}

	@Override
	public Boolean existsByEmail(String email) {
		return userRepositoryInternal.existsByEmail(email);
	}
}
