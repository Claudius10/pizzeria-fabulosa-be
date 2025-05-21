package org.pizzeria.fabulosa.web.service.internal;

import org.pizzeria.fabulosa.common.entity.user.User;

import java.util.Optional;

public interface UserServiceInternal {

	Optional<User> findUserById(Long userId);

	Optional<User> findUserByEmail(String email);

	User findReference(Long userId);

	Boolean existsByEmail(String email);
}
