package org.pizzeria.fabulosa.web.service.user;

import org.pizzeria.fabulosa.common.entity.user.User;
import org.pizzeria.fabulosa.web.dto.user.RegisterDTO;

public interface UserService {

	void createUser(RegisterDTO registerDTO);

	boolean deleteUserById(String password, Long userId);

	// for internal use only

	User findUserById(Long userId);

	User findUserReference(Long userId);

	void existsOrThrow(Long userId);
}