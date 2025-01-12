package org.pizzeria.fabulosa.services.order;

import jakarta.transaction.Transactional;
import org.pizzeria.fabulosa.entity.address.Address;
import org.pizzeria.fabulosa.entity.cart.Cart;
import org.pizzeria.fabulosa.entity.cart.CartItem;
import org.pizzeria.fabulosa.entity.order.Order;
import org.pizzeria.fabulosa.entity.user.User;
import org.pizzeria.fabulosa.repos.order.OrderRepository;
import org.pizzeria.fabulosa.services.address.AddressService;
import org.pizzeria.fabulosa.services.user.UserService;
import org.pizzeria.fabulosa.utils.TimeUtils;
import org.pizzeria.fabulosa.web.constants.ApiResponses;
import org.pizzeria.fabulosa.web.constants.SecurityResponses;
import org.pizzeria.fabulosa.web.dto.order.dto.*;
import org.pizzeria.fabulosa.web.dto.order.projection.OrderSummaryProjection;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;

	private final AddressService addressService;

	private final UserService userService;

	public OrderServiceImpl(OrderRepository orderRepository, AddressService addressService, UserService userService) {
		this.orderRepository = orderRepository;
		this.addressService = addressService;
		this.userService = userService;
	}

	@Override
	public Optional<OrderDTO> findProjectionById(Long orderId) {
		return orderRepository.findOrderById(orderId);
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
				.withFormattedCreatedOn(TimeUtils.getNowAsStringAccountingDST())
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
				.withFormattedCreatedOn(TimeUtils.getNowAsStringAccountingDST())
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
	public boolean updateUserOrder(Long orderId, UpdateUserOrderDTO updateUserOrder) {
		Optional<Address> dbAddress = addressService.findAddressById(updateUserOrder.addressId());
		Optional<Order> dbOrder = findUserOrderById(orderId);

		if (dbOrder.isEmpty() || dbAddress.isEmpty()) {
			return false;
		}

		Address address = dbAddress.get();
		Order order = dbOrder.get();

		boolean pendingAddressUpdate = !order.getAddress().contentEquals(address);
		boolean pendingOrderDetailsUpdate = !order.getOrderDetails().contentEquals(updateUserOrder.orderDetails());
		boolean pendingCartUpdate = updateUserOrder.cart() != null && !order.getCart().contentEquals(updateUserOrder.cart());

		if (pendingAddressUpdate) {
			order.setAddress(address);
		}

		if (pendingOrderDetailsUpdate) {
			order.getOrderDetails().setDeliveryTime(updateUserOrder.orderDetails().getDeliveryTime());
			order.getOrderDetails().setPaymentMethod(updateUserOrder.orderDetails().getPaymentMethod());
			order.getOrderDetails().setBillToChange(updateUserOrder.orderDetails().getBillToChange());
			order.getOrderDetails().setChangeToGive(updateUserOrder.orderDetails().getChangeToGive());
			order.getOrderDetails().setComment(updateUserOrder.orderDetails().getComment());
		}

		// cart == null if !isCartUpdateValid
		if (pendingCartUpdate) {
			order.getCart().setTotalQuantity(updateUserOrder.cart().getTotalQuantity());
			order.getCart().setTotalCost(updateUserOrder.cart().getTotalCost());
			order.getCart().setTotalCostOffers(updateUserOrder.cart().getTotalCostOffers());
			order.getCart().getCartItems().clear();
			for (CartItem item : updateUserOrder.cart().getCartItems()) {
				order.getCart().addItem(item);
			}
		}

		if (pendingAddressUpdate || pendingOrderDetailsUpdate || pendingCartUpdate) {
			order.setUpdatedOn(LocalDateTime.now());
			order.setFormattedUpdatedOn(LocalDateTime.now().plusHours(2).format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy")));
		}

		return true;
	}

	@Override
	public void deleteUserOrderById(Long orderId) {
		Optional<Order> dbOrder = findUserOrderById(orderId);
		// NOTE - there's no need to check whatever order is present here because if it is, OrderValidatorImpl (AOP
		//  ValidateOrderOperation) will catch it before this method is called
		orderRepository.delete(dbOrder.get());
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

	// for internal use only

	@Override
	public Optional<Order> findUserOrderById(Long orderId) {
		return orderRepository.findUserOrderById(orderId);
	}

	@Override
	public LocalDateTime findCreatedOnById(Long orderId) {
		Optional<CreatedOnDTO> createdOn = orderRepository.findCreatedOnById(orderId);

		if (createdOn.isEmpty()) {
			throw new DataRetrievalFailureException(ApiResponses.ORDER_NOT_FOUND);
		}

		return createdOn.get().createdOn();
	}
}