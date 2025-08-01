package org.pizzeria.fabulosa.web.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pizzeria.fabulosa.common.entity.address.Address;
import org.pizzeria.fabulosa.common.entity.user.User;
import org.pizzeria.fabulosa.common.service.role.RoleService;
import org.pizzeria.fabulosa.web.service.address.AddressService;
import org.pizzeria.fabulosa.web.service.dummy.DummyService;
import org.pizzeria.fabulosa.web.service.internal.UserServiceInternal;
import org.pizzeria.fabulosa.web.service.order.OrderService;
import org.pizzeria.fabulosa.web.service.user.UserAddressService;
import org.pizzeria.fabulosa.web.service.user.UserService;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.pizzeria.fabulosa.common.util.constant.AppConstants.DUMMY_ACCOUNT_EMAIL;

@ExtendWith(MockitoExtension.class)
public class DummyServiceTests {

	@Mock
	private UserService userService;

	@Mock
	private UserAddressService userAddressService;

	@Mock
	private UserServiceInternal userServiceInternal;

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
		doReturn(false).when(userServiceInternal).existsByEmail(DUMMY_ACCOUNT_EMAIL);
		doReturn(Optional.of(user)).when(userServiceInternal).findUserByEmail(DUMMY_ACCOUNT_EMAIL);
		doReturn(Optional.of(address)).when(addressService).findByExample(address);

		// Act

		dummyService.init();

		// Assert

		verify(roleService, times(1)).findByName("USER");
		verify(roleService, times(1)).findByName("ADMIN");
		verify(userServiceInternal, times(1)).findUserByEmail(DUMMY_ACCOUNT_EMAIL);
		verify(addressService, times(1)).findByExample(address);
	}

	@Test
	void givenUserExistence_thenDontCreateUser() {

		// Arrange

		doReturn(Optional.empty()).when(roleService).findByName("USER");
		doReturn(Optional.empty()).when(roleService).findByName("ADMIN");
		doReturn(true).when(userServiceInternal).existsByEmail(DUMMY_ACCOUNT_EMAIL);

		// Act

		dummyService.init();

		// Assert

		verify(roleService, times(1)).findByName("USER");
		verify(roleService, times(1)).findByName("ADMIN");
	}
}
