package org.pizzeria.fabulosa.services.user;

import org.pizzeria.fabulosa.entity.address.Address;
import org.pizzeria.fabulosa.entity.user.User;
import org.pizzeria.fabulosa.web.dto.auth.RegisterDTO;
import org.pizzeria.fabulosa.web.dto.user.dto.UserDTO;

import java.util.Optional;
import java.util.Set;

public interface UserService {

	void createUser(RegisterDTO registerDTO);

	Set<Address> findUserAddressListById(Long userId);

	boolean addUserAddress(Long userId, Address address);

	boolean removeUserAddress(Long userId, Long addressId);

	Optional<UserDTO> findUserDTOById(Long userId);

	void updateUserName(String password, Long userId, String name);

	void updateUserEmail(String password, Long userId, String email);

	void updateUserContactNumber(String password, Long userId, Integer contactNumber);

	void updateUserPassword(String password, Long userId, String newPassword);

	boolean deleteUserById(String password, Long userId);

	// for internal use only

	User findUserReference(Long userId);

	User findUserOrThrow(Long userId);

	boolean existsById(Long userId);
}