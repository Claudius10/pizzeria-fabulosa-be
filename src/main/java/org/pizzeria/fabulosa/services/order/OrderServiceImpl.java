package org.pizzeria.fabulosa.services.order;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.entity.address.Address;
import org.pizzeria.fabulosa.entity.cart.Cart;
import org.pizzeria.fabulosa.entity.order.Order;
import org.pizzeria.fabulosa.entity.user.User;
import org.pizzeria.fabulosa.repos.order.OrderRepository;
import org.pizzeria.fabulosa.services.address.AddressService;
import org.pizzeria.fabulosa.services.user.UserService;
import org.pizzeria.fabulosa.utils.TimeUtils;
import org.pizzeria.fabulosa.web.constants.SecurityResponses;
import org.pizzeria.fabulosa.web.dto.order.dto.*;
import org.pizzeria.fabulosa.web.dto.order.projection.OrderSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;

	private final AddressService addressService;

	private final UserService userService;

	@Override
	public Optional<OrderDTO> findOrderDTOById(Long orderId) {
		return orderRepository.findOrderDTOById(orderId);
	}

	@Override
	public CreatedOrderDTO createAnonOrder(NewAnonOrderDTO newAnonOrder) {
		Optional<Address> dbAddress = addressService.findByExample(newAnonOrder.address());

		Cart cart = new Cart.Builder()
				.withTotalQuantity(newAnonOrder.cart().getTotalQuantity())
				.withTotalCost(newAnonOrder.cart().getTotalCost())
				.withTotalCostOffers(newAnonOrder.cart().getTotalCostOffers())
				.withCartItems(newAnonOrder.cart().getCartItems())
				.build();

		Order anonOrder = new Order.Builder()
				.withCreatedOn(LocalDateTime.now())
				.withFormattedCreatedOn(TimeUtils.formatDateAsString(TimeUtils.getNowAccountingDST()))
				.withAnonCustomer(
						newAnonOrder.customer().name(),
						newAnonOrder.customer().contactNumber(),
						newAnonOrder.customer().email())
				.withOrderDetails(newAnonOrder.orderDetails())
				.withCart(cart)
				.build();

		if (dbAddress.isPresent()) {
			anonOrder.setAddress(dbAddress.get());
		} else {
			anonOrder.setAddress(newAnonOrder.address());
		}

		Order order = orderRepository.save(anonOrder);


		return new CreatedOrderDTO(
				order.getId(),
				order.getFormattedCreatedOn(),
				new CustomerDTO(
						order.getAnonCustomerName(),
						order.getAnonCustomerContactNumber(),
						order.getAnonCustomerEmail()
				),
				order.getAddress(),
				order.getOrderDetails(),
				order.getCart()
		);
	}

	@Override
	public CreatedOrderDTO createUserOrder(Long userId, NewUserOrderDTO newUserOrder) {
		User user = userService.findUserOrThrow(userId);
		Optional<Address> address = addressService.findAddressById(newUserOrder.addressId());

		Cart cart = new Cart.Builder()
				.withTotalQuantity(newUserOrder.cart().getTotalQuantity())
				.withTotalCost(newUserOrder.cart().getTotalCost())
				.withTotalCostOffers(newUserOrder.cart().getTotalCostOffers())
				.withCartItems(newUserOrder.cart().getCartItems())
				.build();

		Order order = new Order.Builder()
				.withCreatedOn(LocalDateTime.now())
				.withFormattedCreatedOn(TimeUtils.formatDateAsString(TimeUtils.getNowAccountingDST()))
				.withUser(user)
				.withAddress(address.orElse(null))
				.withCart(cart)
				.withOrderDetails(newUserOrder.orderDetails())
				.build();

		Order newOrder = orderRepository.save(order);
		return new CreatedOrderDTO(
				newOrder.getId(),
				newOrder.getFormattedCreatedOn(),
				new CustomerDTO(
						user.getName(),
						user.getContactNumber(),
						user.getEmail()
				),
				address.orElse(null),
				newOrder.getOrderDetails(),
				newOrder.getCart()
		);
	}

	@Override
	public void deleteUserOrderById(Long orderId) {
		orderRepository.deleteById(orderId);
	}

	@Override
	public Page<OrderSummaryProjection> findUserOrderSummary(Long userId, int size, int page) {
		if (!userService.existsById(userId)) {
			throw new UsernameNotFoundException(SecurityResponses.USER_NOT_FOUND); // this ends up as AuthenticationException
		}

		Sort.TypedSort<Order> order = Sort.sort(Order.class);
		Sort sort = order.by(Order::getId).descending();
		PageRequest pageRequest = PageRequest.of(page, size, sort);
		return orderRepository.findAllByUser_Id(userId, pageRequest);
	}
}