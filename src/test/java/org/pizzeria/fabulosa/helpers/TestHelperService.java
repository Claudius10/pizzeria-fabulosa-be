package org.pizzeria.fabulosa.helpers;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.pizzeria.fabulosa.common.dao.order.OrderRepository;
import org.pizzeria.fabulosa.common.entity.address.Address;
import org.pizzeria.fabulosa.common.entity.cart.Cart;
import org.pizzeria.fabulosa.common.entity.order.Order;
import org.pizzeria.fabulosa.common.entity.order.OrderDetails;
import org.pizzeria.fabulosa.common.entity.projection.OrderProjection;
import org.pizzeria.fabulosa.common.entity.user.User;
import org.pizzeria.fabulosa.web.dto.order.CartItemDTO;
import org.pizzeria.fabulosa.web.dto.user.RegisterDTO;
import org.pizzeria.fabulosa.web.service.address.AddressService;
import org.pizzeria.fabulosa.web.service.internal.UserServiceInternal;
import org.pizzeria.fabulosa.web.service.user.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Service
@Transactional
@AllArgsConstructor
public class TestHelperService {

	private final OrderRepository orderRepository;

	private final UserService userService;

	private final AddressService addressService;

	private final UserServiceInternal userServiceInternal;

	public OrderProjection createOrder(Long userId, Long addressId, int minusMins) {
		LocalDateTime createdOn = LocalDateTime.now().minusMinutes(minusMins);

		Cart cart = new Cart.Builder()
				.withTotalQuantity(1)
				.withTotalCost(18.30D)
				.withTotalCostOffers(0D)
				.withCartItems(List.of(new CartItemDTO(
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
				)))
				.build();

		Order order = new Order.Builder()
				.withCreatedOn(createdOn)
				.withFormattedCreatedOn(createdOn.format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy")))
				.withUser(userServiceInternal.findReference(userId))
				.withAddress(addressService.findReference(addressId))
				.withOrderDetails(OrderDetails.builder()
						.withDeliveryTime("ASAP")
						.withPaymentMethod("Card")
						.withBillToChange(null)
						.withComment(null)
						.withStorePickUp(false)
						.withChangeToGive(0D).
						build())
				.withCart(cart)
				.build();
		Long id = orderRepository.save(order).getId();
		return findOrder(id);
	}

	public Long createUser(String email) {

		RegisterDTO registerDTO = new RegisterDTO(
				"Tester",
				email,
				email,
				123456789,
				"Password1",
				"Password1");

		userService.createUser(registerDTO);

		Optional<User> user = userServiceInternal.findUserByEmail(email);
		assertThat(user.isPresent()).isTrue();
		return user.get().getId();
	}

	public Long createAddress(String streetName, int streetNumber) {
		return addressService.create(Address.builder()
				.withStreet(streetName)
				.withNumber(streetNumber)
				.build());
	}

	public OrderProjection findOrder(Long orderId) {
		return orderRepository.findOrderDTOById(orderId).orElse(null);
	}
}
