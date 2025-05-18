package org.pizzeria.fabulosa.web.service.order;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.common.dao.order.OrderRepository;
import org.pizzeria.fabulosa.common.entity.address.Address;
import org.pizzeria.fabulosa.common.entity.cart.Cart;
import org.pizzeria.fabulosa.common.entity.dto.CreatedOnDTO;
import org.pizzeria.fabulosa.web.dto.order.CreatedOrderDTO;
import org.pizzeria.fabulosa.common.entity.dto.OrderDTO;
import org.pizzeria.fabulosa.common.entity.dto.OrderSummaryProjection;
import org.pizzeria.fabulosa.common.entity.order.Order;
import org.pizzeria.fabulosa.common.entity.order.OrderDetails;
import org.pizzeria.fabulosa.common.entity.user.User;
import org.pizzeria.fabulosa.common.util.TimeUtils;
import org.pizzeria.fabulosa.web.dto.order.*;
import org.pizzeria.fabulosa.web.service.address.AddressService;
import org.pizzeria.fabulosa.web.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.pizzeria.fabulosa.web.util.OrderUtils.fromOrderToDTO;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;

	private final AddressService addressService;

	private final UserService userService;

	@Override
	public Optional<UserOrderDTO> findOrderDTOById(Long orderId) {

		Optional<OrderDTO> orderDTO = orderRepository.findOrderDTOById(orderId);

		if (orderDTO.isEmpty()) {
			return Optional.empty();
		}

		OrderDTO order = orderDTO.get();

		return Optional.of(new UserOrderDTO(
				order.id(),
				order.createdOn(),
				order.formattedCreatedOn(),
				new AddressDTO(
						order.address().getId(),
						order.address().getStreet(),
						order.address().getNumber(),
						order.address().getDetails()
				),
				new OrderDetailsDTO(
						order.orderDetails().getDeliveryTime(),
						order.orderDetails().getPaymentMethod(),
						order.orderDetails().getBillToChange(),
						order.orderDetails().getComment(),
						order.orderDetails().getStorePickUp(),
						order.orderDetails().getChangeToGive()
				),
				new CartDTO(
						order.cart().getTotalQuantity(),
						order.cart().getTotalCost(),
						order.cart().getTotalCostOffers(),
						order.cart().getCartItems().stream().map(cartItem -> new CartItemDTO(
										cartItem.getType(),
										cartItem.getPrice(),
										cartItem.getQuantity(),
										cartItem.getName(),
										cartItem.getDescription(),
										cartItem.getFormats()
								))
								.collect(Collectors.toList())
				)
		));
	}

	@Override
	public CreatedOrderDTO createAnonOrder(NewAnonOrderDTO newAnonOrder) {

		Address unknownAddress = Address.fromDTOBuilder().build(newAnonOrder.address());

		Optional<Address> dbAddress = addressService.findByExample(unknownAddress);

		Cart cart = new Cart.Builder()
				.withTotalQuantity(newAnonOrder.cart().totalQuantity())
				.withTotalCost(newAnonOrder.cart().totalCost())
				.withTotalCostOffers(newAnonOrder.cart().totalCostOffers())
				.withCartItems(newAnonOrder.cart().cartItems())
				.build();

		Order anonOrder = new Order.Builder()
				.withCreatedOn(LocalDateTime.now())
				.withFormattedCreatedOn(TimeUtils.formatDateAsString(TimeUtils.getNowAccountingDST()))
				.withAnonCustomer(
						newAnonOrder.customer().name(),
						newAnonOrder.customer().contactNumber(),
						newAnonOrder.customer().email())
				.withOrderDetails(OrderDetails.fromDTOBuilder().build(newAnonOrder.orderDetails()))
				.withCart(cart)
				.build();

		if (dbAddress.isPresent()) {
			anonOrder.setAddress(dbAddress.get());
		} else {
			anonOrder.setAddress(unknownAddress);
		}

		Order order = orderRepository.save(anonOrder);

		return fromOrderToDTO(order);
	}

	@Override
	public CreatedOrderDTO createUserOrder(Long userId, NewUserOrderDTO newUserOrder) {
		User user = userService.findUserById(userId);
		Address address = addressService.findReference(newUserOrder.addressId());

		Cart cart = new Cart.Builder()
				.withTotalQuantity(newUserOrder.cart().totalQuantity())
				.withTotalCost(newUserOrder.cart().totalCost())
				.withTotalCostOffers(newUserOrder.cart().totalCostOffers())
				.withCartItems(newUserOrder.cart().cartItems())
				.build();

		Order order = new Order.Builder()
				.withCreatedOn(LocalDateTime.now())
				.withFormattedCreatedOn(TimeUtils.formatDateAsString(TimeUtils.getNowAccountingDST()))
				.withUser(user)
				.withAddress(address)
				.withCart(cart)
				.withOrderDetails(OrderDetails.fromDTOBuilder().build(newUserOrder.orderDetails()))
				.build();

		Order newOrder = orderRepository.save(order);

		return fromOrderToDTO(newOrder);
	}

	@Override
	public void deleteUserOrderById(Long orderId) {
		orderRepository.deleteById(orderId);
	}

	@Override
	public Page<OrderSummaryProjection> findUserOrderSummary(Long userId, int size, int page) {
		userService.existsOrThrow(userId);
		Sort.TypedSort<Order> order = Sort.sort(Order.class);
		Sort sort = order.by(Order::getId).descending();
		PageRequest pageRequest = PageRequest.of(page, size, sort);
		return orderRepository.findAllByUser_Id(userId, pageRequest);
	}

	@Override
	public Optional<CreatedOnDTO> findCreatedOnDTOById(Long orderId) {
		return orderRepository.findCreatedOnById(orderId);
	}
}