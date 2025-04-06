package org.pizzeria.fabulosa.services.user;

import org.pizzeria.fabulosa.entity.user.User;
import org.pizzeria.fabulosa.web.dto.user.dto.RegisterDTO;
import org.pizzeria.fabulosa.web.dto.user.dto.UserDTO;

import java.util.Optional;

public interface UserService {

	void createUser(RegisterDTO registerDTO);

	Optional<UserDTO> findUserDTOById(Long userId);

	boolean deleteUserById(String password, Long userId);

	// for internal use only

	User findUserById(Long userId);

	User findUserReference(Long userId);

	void existsOrThrow(Long userId);
}