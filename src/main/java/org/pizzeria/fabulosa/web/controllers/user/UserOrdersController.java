package org.pizzeria.fabulosa.web.controllers.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.common.entity.projection.CreatedOnProjection;
import org.pizzeria.fabulosa.common.entity.projection.OrderSummaryProjection;
import org.pizzeria.fabulosa.security.utils.UserSecurity;
import org.pizzeria.fabulosa.web.controllers.user.swagger.UserOrdersControllerSwagger;
import org.pizzeria.fabulosa.web.dto.order.*;
import org.pizzeria.fabulosa.web.service.order.OrderService;
import org.pizzeria.fabulosa.web.util.constant.ApiRoutes;
import org.pizzeria.fabulosa.web.validation.order.CompositeValidator;
import org.pizzeria.fabulosa.web.validation.order.OrderValidatorInput;
import org.pizzeria.fabulosa.web.validation.order.ValidationResult;
import org.pizzeria.fabulosa.web.validation.order.Validator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.pizzeria.fabulosa.web.util.ResponseUtils.error;

@RestController
@RequestMapping(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.ORDER_BASE)
@RequiredArgsConstructor
public class UserOrdersController implements UserOrdersControllerSwagger {

	private final OrderService orderService;

	private final CompositeValidator<OrderValidatorInput> newOrderValidator;

	private final Validator<LocalDateTime> deleteOrderValidator;

	@PostMapping
	public ResponseEntity<?> createUserOrder(@RequestBody @Valid NewUserOrderDTO order, @PathVariable Long userId, HttpServletRequest request) {

		if (!UserSecurity.valid(userId)) {
			return UserSecurity.deny(request);
		}

		Optional<ValidationResult> validate = newOrderValidator.validate(new OrderValidatorInput(order.cart(), order.orderDetails()));

		if (validate.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error(this.getClass().getSimpleName(), validate.get().message(), request.getPathInfo()));
		}

		CreatedOrderDTO createdOrder = orderService.createUserOrder(userId, order);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
	}

	@GetMapping(ApiRoutes.ORDER_ID)
	public ResponseEntity<?> findUserOrderDTO(@PathVariable Long orderId, @PathVariable Long userId, HttpServletRequest request) {

		if (!UserSecurity.valid(userId)) {
			return UserSecurity.deny(request);
		}

		Optional<OrderDTO> order = orderService.findOrderDTOById(orderId);

		return order.map(orderDTO -> ResponseEntity.ok().body(orderDTO)).orElse(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
	}

	@DeleteMapping(ApiRoutes.ORDER_ID)
	public ResponseEntity<?> deleteUserOrderById(@PathVariable Long orderId, @PathVariable Long userId, HttpServletRequest request) {

		if (!UserSecurity.valid(userId)) {
			return UserSecurity.deny(request);
		}

		Optional<CreatedOnProjection> createdOnDTOById = orderService.findCreatedOnDTOById(orderId);

		if (createdOnDTOById.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} else {
			ValidationResult result = deleteOrderValidator.validate(createdOnDTOById.get().getCreatedOn());
			if (!result.valid()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error(this.getClass().getSimpleName(), result.message(), request.getPathInfo()));
			}
		}

		orderService.deleteUserOrderById(orderId);
		return ResponseEntity.ok(orderId);
	}

	@GetMapping(ApiRoutes.ORDER_SUMMARY)
	public ResponseEntity<?> findUserOrdersSummary(
			@RequestParam(name = ApiRoutes.PAGE_NUMBER) Integer pageNumber,
			@RequestParam(name = ApiRoutes.PAGE_SIZE) Integer pageSize,
			@PathVariable Long userId,
			HttpServletRequest request) {

		if (!UserSecurity.valid(userId)) {
			return UserSecurity.deny(request);
		}

		Page<OrderSummaryProjection> orderSummaryPage = orderService.findUserOrderSummary(userId, pageSize, pageNumber);

		if (orderSummaryPage.getTotalElements() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		List<OrderSummaryDTO> orderSummaryList = orderSummaryPage.getContent().stream().map(orderSummary -> new OrderSummaryDTO(
				orderSummary.getId(),
				orderSummary.getFormattedCreatedOn(),
				orderSummary.getOrderDetails().getPaymentMethod(),
				orderSummary.getCart().getTotalQuantity(),
				orderSummary.getCart().getTotalCost(),
				orderSummary.getCart().getTotalCostOffers()
		)).collect(Collectors.toList());

		OrderSummaryListDTO orders = new OrderSummaryListDTO(
				orderSummaryList,
				orderSummaryPage.getTotalPages(),
				orderSummaryPage.getPageable().getPageSize(),
				orderSummaryPage.getTotalElements(),
				orderSummaryPage.hasNext()
		);

		return ResponseEntity.ok(orders);
	}
}