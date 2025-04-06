package org.pizzeria.fabulosa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pizzeria.fabulosa.entity.address.Address;
import org.pizzeria.fabulosa.entity.user.User;
import org.pizzeria.fabulosa.repos.user.UserRepository;
import org.pizzeria.fabulosa.services.address.AddressService;
import org.pizzeria.fabulosa.services.dummy.DummyService;
import org.pizzeria.fabulosa.services.order.OrderService;
import org.pizzeria.fabulosa.services.role.RoleService;
import org.pizzeria.fabulosa.services.user.UserAddressService;
import org.pizzeria.fabulosa.services.user.UserService;
import org.pizzeria.fabulosa.utils.Constants;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DummyServiceTests {

	@Mock
	private UserService userService;

	@Mock
	private UserAddressService userAddressService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private OrderService orderService;

	@Mock
	private RoleService roleService;

	@Mock
	private AddressService addressService;

	@InjectMocks
	private DummyService dummyService;

	@Test
	void givenInit_thenSetupRolesAndDummyUser() {

		// Arrange

		User user = new User();
		user.setId(1L);

		Address address = Address.builder()
				.withStreet("En un lugar de la Mancha...")
				.withNumber(1605)
				.build();

		doReturn(Optional.empty()).when(roleService).findByName("USER");
		doReturn(Optional.empty()).when(roleService).findByName("ADMIN");
		doReturn(user).when(userRepository).findUserByEmail(Constants.DUMMY_ACCOUNT_EMAIL);
		doReturn(Optional.of(address)).when(addressService).findByExample(address);

		// Act

		dummyService.init();

		// Assert

		verify(roleService, times(1)).findByName("USER");
		verify(roleService, times(1)).findByName("ADMIN");
		verify(userRepository, times(1)).findUserByEmail(Constants.DUMMY_ACCOUNT_EMAIL);
		verify(addressService, times(1)).findByExample(address);
	}

	@Test
	void givenUserExistence_thenDontCreateUser() {

		// Arrange

		doReturn(Optional.empty()).when(roleService).findByName("USER");
		doReturn(Optional.empty()).when(roleService).findByName("ADMIN");
		doReturn(true).when(userRepository).exists(any());

		// Act

		dummyService.init();

		// Assert

		verify(roleService, times(1)).findByName("USER");
		verify(roleService, times(1)).findByName("ADMIN");
		verify(userRepository, times(1)).exists(any());
	}
}
