package org.pizzeria.fabulosa.web.service.dummy;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pizzeria.fabulosa.common.dao.user.UserRepository;
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
import org.pizzeria.fabulosa.web.service.order.OrderService;
import org.pizzeria.fabulosa.web.service.user.UserAddressService;
import org.pizzeria.fabulosa.web.service.user.UserService;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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

	private final UserRepository userRepository;

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

		log.info("Roles setup done");
	}

	private void setupDummyUser() {
		if (!exists()) {
			createDummyUser();
			User user = userRepository.findUserByEmail(DUMMY_ACCOUNT_EMAIL);
			log.info("Dummy user setup: id is {}", user.getId());
			addAddress(user.getId());
			addOrder(user.getId());
			log.info("Dummy user setup: done");
		} else {
			log.info("Dummy user setup: nothing to do");
		}
	}

	private boolean exists() {
		User it = new User();
		it.setEmail(DUMMY_ACCOUNT_EMAIL);
		ExampleMatcher matcher = ExampleMatcher.matching()
				.withIgnoreNullValues()
				.withStringMatcher(ExampleMatcher.StringMatcher.EXACT);

		Example<User> example = Example.of(it, matcher);
		return userRepository.exists(example);
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
		log.info("Adding address to dummy user");
		userAddressService.addUserAddress(userId, Address.builder()
				.withStreet("En un lugar de la Mancha...")
				.withNumber(1605)
				.build()
		);
	}

	private void addOrder(Long userId) {
		log.info("Adding order to dummy user");

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
	}
}