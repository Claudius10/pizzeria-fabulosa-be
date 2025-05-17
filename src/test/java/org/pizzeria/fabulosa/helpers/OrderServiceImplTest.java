package org.pizzeria.fabulosa.helpers;

import jakarta.transaction.Transactional;
import org.pizzeria.fabulosa.common.dao.order.OrderRepository;
import org.pizzeria.fabulosa.common.entity.address.Address;
import org.pizzeria.fabulosa.common.entity.cart.Cart;
import org.pizzeria.fabulosa.common.entity.order.Order;
import org.pizzeria.fabulosa.common.entity.order.OrderDetails;
import org.pizzeria.fabulosa.common.entity.user.User;
import org.pizzeria.fabulosa.web.dto.order.NewUserOrderDTO;
import org.pizzeria.fabulosa.web.service.address.AddressService;
import org.pizzeria.fabulosa.web.service.user.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
public class OrderServiceImplTest {

	private final OrderRepository orderRepository;

	private final AddressService addressService;

	private final UserService userService;

	public OrderServiceImplTest(
			OrderRepository orderRepository,
			AddressService addressService,
			UserService userService) {
		this.orderRepository = orderRepository;
		this.addressService = addressService;
		this.userService = userService;
	}

	public Long createOrderTestSubjects(NewUserOrderDTO newUserOrder, Long userId, LocalDateTime createdOn) {
		User user = userService.findUserReference(userId);
		Address address = addressService.findReference(newUserOrder.addressId());

		Cart cart = new Cart.Builder()
				.withTotalQuantity(newUserOrder.cart().totalQuantity())
				.withTotalCost(newUserOrder.cart().totalCost())
				.withTotalCostOffers(newUserOrder.cart().totalCostOffers())
				.withCartItems(newUserOrder.cart().cartItems())
				.build();

		Order order = new Order.Builder()
				.withCreatedOn(createdOn)
				.withFormattedCreatedOn(createdOn.plusHours(1).format(DateTimeFormatter.ofPattern("HH:mm - " +
						"dd/MM/yyyy")))
				.withUser(user)
				.withAddress(address)
				.withOrderDetails(OrderDetails.fromDTOBuilder().build(newUserOrder.orderDetails()))
				.withCart(cart)
				.build();

		return orderRepository.save(order).getId();
	}
}
