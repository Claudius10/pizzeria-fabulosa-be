package org.pizzeria.fabulosa.web.controllers.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.security.utils.UserSecurity;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.order.dto.*;
import org.pizzeria.fabulosa.web.dto.order.projection.OrderSummaryProjection;
import org.pizzeria.fabulosa.web.order.validation.OrderCartValidator;
import org.pizzeria.fabulosa.web.order.validation.OrderDetailsValidator;
import org.pizzeria.fabulosa.web.order.validation.OrderValidationResult;
import org.pizzeria.fabulosa.web.order.validation.OrderValidator;
import org.pizzeria.fabulosa.web.service.order.OrderService;
import org.pizzeria.fabulosa.web.util.constant.ApiResponses;
import org.pizzeria.fabulosa.web.util.constant.ApiRoutes;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.pizzeria.fabulosa.web.util.ResponseUtils.error;

@RestController
@RequestMapping(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.ORDER_BASE)
@Tag(name = "User orders", description = "User order CRUD")
@RequiredArgsConstructor
@Validated
public class UserOrdersController {

	private final OrderService orderService;

	@PostMapping
	@Operation(summary = "Create user order")
	public ResponseEntity<Response> createUserOrder(
			@RequestBody @Valid NewUserOrderDTO order,
			@Parameter(description = "Id of the user") @PathVariable Long userId,
			HttpServletRequest request
	) {

		if (!UserSecurity.valid(userId)) {
			return UserSecurity.deny(request);
		}

		OrderValidationResult cart = OrderCartValidator.validate(order.cart());
		if (!cart.isValid()) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(this.getClass().getSimpleName(), cart.getMessage(), request.getPathInfo()));
		}

		OrderValidationResult orderDetails = OrderDetailsValidator.validate(order.cart(), order.orderDetails());
		if (!orderDetails.isValid()) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(this.getClass().getSimpleName(), orderDetails.getMessage(), request.getPathInfo()));
		}

		CreatedOrderDTO createdOrder = orderService.createUserOrder(userId, order);
		return ResponseEntity.status(HttpStatus.CREATED).body(Response.builder().payload(createdOrder).build());
	}

	@GetMapping(ApiRoutes.ORDER_ID)
	public ResponseEntity<Response> findUserOrderDTO(@PathVariable Long orderId, @PathVariable Long userId, HttpServletRequest request) {
		if (!UserSecurity.valid(userId)) {
			return UserSecurity.deny(request);
		}

		Optional<OrderDTO> projectionById = orderService.findOrderDTOById(orderId);

		return projectionById.map(orderDTO -> ResponseEntity.ok().body(Response.builder().payload(orderDTO).build()))
				.orElse(ResponseEntity.status(HttpStatus.NO_CONTENT).body(error(this.getClass().getSimpleName(), ApiResponses.ORDER_NOT_FOUND, request.getPathInfo())));
	}

	@DeleteMapping(ApiRoutes.ORDER_ID)
	public ResponseEntity<Response> deleteUserOrderById(@PathVariable Long orderId, @PathVariable Long userId, HttpServletRequest request) {

		if (!UserSecurity.valid(userId)) {
			return UserSecurity.deny(request);
		}

		Optional<CreatedOnDTO> createdOnDTOById = orderService.findCreatedOnDTOById(orderId);

		if (createdOnDTOById.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error(this.getClass().getSimpleName(), ApiResponses.ORDER_NOT_FOUND, request.getPathInfo()));
		} else {
			OrderValidationResult result = OrderValidator.validateDelete(createdOnDTOById.get().createdOn());
			if (!result.isValid()) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(this.getClass().getSimpleName(), result.getMessage(), request.getPathInfo()));
			}
		}

		orderService.deleteUserOrderById(orderId);
		return ResponseEntity.ok(Response.builder().payload(orderId).build());
	}

	@GetMapping(ApiRoutes.ORDER_SUMMARY)
	public ResponseEntity<Response> findUserOrdersSummary(
			@RequestParam(name = ApiRoutes.PAGE_NUMBER) Integer pageNumber,
			@RequestParam(name = ApiRoutes.PAGE_SIZE) Integer pageSize,
			@PathVariable Long userId,
			HttpServletRequest request) {
		if (!UserSecurity.valid(userId)) {
			return UserSecurity.deny(request);
		}

		Page<OrderSummaryProjection> orderSummaryPage = orderService.findUserOrderSummary(userId, pageSize, pageNumber);

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

		return ResponseEntity.ok(Response.builder().payload(orders).build());
	}
}