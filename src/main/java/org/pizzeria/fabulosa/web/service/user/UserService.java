package org.pizzeria.fabulosa.web.service.user;

import org.pizzeria.fabulosa.web.dto.user.RegisterDTO;

public interface UserService {

	void createUser(RegisterDTO registerDTO);

	boolean deleteUserById(String password, Long userId);
}