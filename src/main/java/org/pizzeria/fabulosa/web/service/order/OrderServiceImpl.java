package org.pizzeria.fabulosa.web.service.order;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.common.dao.order.OrderRepository;
import org.pizzeria.fabulosa.common.entity.address.Address;
import org.pizzeria.fabulosa.common.entity.cart.Cart;
import org.pizzeria.fabulosa.common.entity.order.Order;
import org.pizzeria.fabulosa.common.entity.order.OrderDetails;
import org.pizzeria.fabulosa.common.entity.projection.CreatedOnProjection;
import org.pizzeria.fabulosa.common.entity.projection.OrderProjection;
import org.pizzeria.fabulosa.common.entity.projection.OrderSummaryProjection;
import org.pizzeria.fabulosa.common.util.TimeUtils;
import org.pizzeria.fabulosa.web.dto.order.*;
import org.pizzeria.fabulosa.web.service.address.AddressService;
import org.pizzeria.fabulosa.web.service.internal.UserServiceInternal;
import org.pizzeria.fabulosa.web.util.OrderUtils;
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

	private final UserServiceInternal userServiceInternal;

	@Override
	public Optional<OrderDTO> findOrderDTOById(Long orderId) {

		Optional<OrderProjection> orderDTO = orderRepository.findOrderDTOById(orderId);

		if (orderDTO.isEmpty()) {
			return Optional.empty();
		}

		OrderProjection order = orderDTO.get();

		return Optional.of(new OrderDTO(
				order.getId(),
				order.getCreatedOn(),
				order.getFormattedCreatedOn(),
				new AddressDTO(
						order.getAddress().getId(),
						order.getAddress().getStreet(),
						order.getAddress().getNumber(),
						order.getAddress().getDetails()
				),
				new OrderDetailsDTO(
						order.getOrderDetails().getDeliveryTime(),
						order.getOrderDetails().getPaymentMethod(),
						order.getOrderDetails().getBillToChange(),
						order.getOrderDetails().getComment(),
						order.getOrderDetails().getStorePickUp(),
						order.getOrderDetails().getChangeToGive()
				),
				new CartDTO(
						order.getCart().getTotalQuantity(),
						order.getCart().getTotalCost(),
						order.getCart().getTotalCostOffers(),
						order.getCart().getCartItems().stream().map(cartItem -> new CartItemDTO(
										cartItem.getId(),
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

		if (null != newAnonOrder.orderDetails().billToChange()) {
			anonOrder.getOrderDetails().setChangeToGive(OrderUtils.calculatePaymentChange(newAnonOrder.orderDetails().billToChange(), newAnonOrder.cart().totalCost(), newAnonOrder.cart().totalCostOffers()));
		}

		Order order = orderRepository.save(anonOrder);

		return fromOrderToDTO(order);
	}

	@Override
	public CreatedOrderDTO createUserOrder(Long userId, NewUserOrderDTO newUserOrder) {
		Cart cart = new Cart.Builder()
				.withTotalQuantity(newUserOrder.cart().totalQuantity())
				.withTotalCost(newUserOrder.cart().totalCost())
				.withTotalCostOffers(newUserOrder.cart().totalCostOffers())
				.withCartItems(newUserOrder.cart().cartItems())
				.build();

		Order order = new Order.Builder()
				.withCreatedOn(LocalDateTime.now())
				.withFormattedCreatedOn(TimeUtils.formatDateAsString(TimeUtils.getNowAccountingDST()))
				.withUser(userServiceInternal.findReference(userId))
				.withAddress(addressService.findReference(newUserOrder.addressId()))
				.withCart(cart)
				.withOrderDetails(OrderDetails.fromDTOBuilder().build(newUserOrder.orderDetails()))
				.build();

		if (null != newUserOrder.orderDetails().billToChange()) {
			order.getOrderDetails().setChangeToGive(OrderUtils.calculatePaymentChange(newUserOrder.orderDetails().billToChange(), newUserOrder.cart().totalCost(), newUserOrder.cart().totalCostOffers()));
		}

		Order newOrder = orderRepository.save(order);

		return fromOrderToDTO(newOrder);
	}

	@Override
	public void deleteUserOrderById(Long orderId) {
		orderRepository.deleteById(orderId);
	}

	@Override
	public Page<OrderSummaryProjection> findUserOrderSummary(Long userId, int size, int page) {
		Sort.TypedSort<Order> order = Sort.sort(Order.class);
		Sort sort = order.by(Order::getId).descending();
		PageRequest pageRequest = PageRequest.of(page, size, sort);
		return orderRepository.findAllByUser_Id(userId, pageRequest);
	}

	@Override
	public Optional<CreatedOnProjection> findCreatedOnDTOById(Long orderId) {
		return orderRepository.findCreatedOnById(orderId);
	}
}