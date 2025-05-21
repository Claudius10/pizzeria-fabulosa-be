package org.pizzeria.fabulosa.web.service.dummy;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pizzeria.fabulosa.common.entity.address.Address;
import org.pizzeria.fabulosa.common.entity.role.Role;
import org.pizzeria.fabulosa.common.entity.user.User;
import org.pizzeria.fabulosa.common.service.role.RoleService;
import org.pizzeria.fabulosa.web.dto.order.CartDTO;
import org.pizzeria.fabulosa.web.dto.order.CartItemDTO;
import org.pizzeria.fabulosa.web.dto.order.NewUserOrderDTO;
import org.pizzeria.fabulosa.web.dto.order.OrderDetailsDTO;
import org.pizzeria.fabulosa.web.dto.user.RegisterDTO;
import org.pizzeria.fabulosa.web.service.address.AddressService;
import org.pizzeria.fabulosa.web.service.internal.UserServiceInternal;
import org.pizzeria.fabulosa.web.service.order.OrderService;
import org.pizzeria.fabulosa.web.service.user.UserAddressService;
import org.pizzeria.fabulosa.web.service.user.UserService;
import org.pizzeria.fabulosa.web.util.constant.ApiResponses;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.pizzeria.fabulosa.common.util.constant.AppConstants.DUMMY_ACCOUNT_EMAIL;

@Service
@RequiredArgsConstructor
@Profile("production")
@Slf4j
public class DummyService {

	private final UserService userService;

	private final UserAddressService userAddressService;

	private final UserServiceInternal userServiceInternal;

	private final OrderService orderService;

	private final RoleService roleService;

	private final AddressService addressService;

	@PostConstruct
	public void init() {
		setupRoles();
		setupDummyUser();
	}

	private void setupRoles() {
		if (roleService.findByName("USER").isEmpty()) {
			this.roleService.createRole(new Role("USER"));
		}

		if (roleService.findByName("ADMIN").isEmpty()) {
			this.roleService.createRole(new Role("ADMIN"));
		}

		log.info("Roles done");
	}

	private void setupDummyUser() {
		if (!exists()) {
			createDummyUser();

			Optional<User> userByEmail = userServiceInternal.findUserByEmail(DUMMY_ACCOUNT_EMAIL);

			if (userByEmail.isEmpty()) {
				throw new EntityNotFoundException(ApiResponses.USER_NOT_FOUND);
			}

			User user = userByEmail.get();
			addAddress(user.getId());
			addOrder(user.getId());
			log.info("Dummy user created with id {}", user.getId());
		} else {
			log.info("Dummy user already exists");
		}
	}

	private boolean exists() {
		return userServiceInternal.existsByEmail(DUMMY_ACCOUNT_EMAIL);
	}

	private void createDummyUser() {
		log.info("Creating dummy user");
		userService.createUser(new RegisterDTO(
				"Miguel de Cervantes",
				DUMMY_ACCOUNT_EMAIL,
				DUMMY_ACCOUNT_EMAIL,
				123456789,
				"Password1",
				"Password1"
		));
	}

	private void addAddress(Long userId) {
		userAddressService.addUserAddress(userId, Address.builder()
				.withStreet("En un lugar de la Mancha...")
				.withNumber(1605)
				.build()
		);
	}

	private void addOrder(Long userId) {
		Address address = Address.builder()
				.withStreet("En un lugar de la Mancha...")
				.withNumber(1605)
				.build();

		Optional<Address> addressByExample = addressService.findByExample(address);
		log.info("Dummy user's address exists {}", addressByExample.isPresent());

		OrderDetailsDTO orderDetailsDTO = new OrderDetailsDTO(
				"form.select.time.asap",
				"form.select.payment.method.cash",
				0D,
				"",
				false,
				0D
		);

		CartItemDTO cartItemDTO = new CartItemDTO(
				null,
				"pizza",
				13.30D,
				1,
				Map.of("es", "Cuatro Quesos", "en", "Cuatro Quesos"),
				Map.of(
						"es", List.of("Salsa de Tomate", "Mozzarella 100%", "Parmesano", "Emmental", "Queso Azul"),
						"en", List.of("Tomato Sauce", "100% Mozzarella", "Parmesan Cheese", "Emmental Cheese", "Blue Cheese")
				),
				Map.of("m", Map.of("es", "Mediana", "en", "Medium"), "l", Map.of(), "s", Map.of())
		);

		CartDTO cartDTO = new CartDTO(
				1,
				13.30D,
				0D,
				List.of(cartItemDTO)
		);

		NewUserOrderDTO order = new NewUserOrderDTO(addressByExample.get().getId(), orderDetailsDTO, cartDTO);

		orderService.createUserOrder(userId, order);
		log.info("Created order for dummy user");
	}
}