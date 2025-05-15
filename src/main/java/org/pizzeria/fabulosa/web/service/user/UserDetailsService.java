package org.pizzeria.fabulosa.web.service.user;

public interface UserDetailsService {

	void updateUserName(String password, Long userId, String name);

	void updateUserEmail(String password, Long userId, String email);

	void updateUserContactNumber(String password, Long userId, Integer contactNumber);

	void updateUserPassword(String password, Long userId, String newPassword);
}
