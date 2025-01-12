package org.pizzeria.fabulosa.web.controllers.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.pizzeria.fabulosa.services.order.OrderService;
import org.pizzeria.fabulosa.web.aop.annotations.ValidateUserId;
import org.pizzeria.fabulosa.web.constants.ApiRoutes;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.api.Status;
import org.pizzeria.fabulosa.web.dto.order.dto.*;
import org.pizzeria.fabulosa.web.dto.order.projection.OrderSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.ORDER_BASE)
@Validated
public class UserOrdersController {

	private final OrderService orderService;

	public UserOrdersController(OrderService orderService) {
		this.orderService = orderService;
	}

	@ValidateUserId
	@PostMapping
	public ResponseEntity<Response> createUserOrder(@RequestBody @Valid NewUserOrderDTO order, @PathVariable Long userId, HttpServletRequest request) {
		CreatedOrderDTO createdOrder = orderService.createUserOrder(userId, order);

		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.CREATED.name())
						.code(HttpStatus.CREATED.value())
						.isError(false)
						.build())
				.payload(createdOrder)
				.build();

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@ValidateUserId
	@GetMapping(ApiRoutes.ORDER_ID)
	public ResponseEntity<Response> findUserOrderDTO(@PathVariable Long orderId, @PathVariable Long userId, HttpServletRequest request) {
		Optional<OrderDTO> projectionById = orderService.findProjectionById(orderId);

		Response response = Response.builder()
				.status(Status.builder()
						.description(projectionById.isPresent() ? HttpStatus.OK.name() : HttpStatus.NO_CONTENT.name())
						.code(projectionById.isPresent() ? HttpStatus.OK.value() : HttpStatus.NO_CONTENT.value())
						.isError(false)
						.build())
				.payload(projectionById.orElse(null))
				.build();

		return ResponseEntity.ok(response);
	}

	@ValidateUserId
	@PutMapping(ApiRoutes.ORDER_ID)
	public ResponseEntity<Response> updateUserOrder(
			@PathVariable Long orderId,
			@RequestBody @Valid UpdateUserOrderDTO order,
			@PathVariable Long userId,
			HttpServletRequest request) {

		boolean result = orderService.updateUserOrder(orderId, order);

		Response response = Response.builder()
				.status(Status.builder()
						.description(result ? HttpStatus.OK.name() : HttpStatus.NO_CONTENT.name())
						.code(result ? HttpStatus.OK.value() : HttpStatus.NO_CONTENT.value())
						.isError(false)
						.build())
				.payload(orderId)
				.build();

		return ResponseEntity.ok(response);
	}

	@ValidateUserId
	@DeleteMapping(ApiRoutes.ORDER_ID)
	public ResponseEntity<Response> deleteUserOrderById(@PathVariable Long orderId, @PathVariable Long userId, HttpServletRequest request) {
		orderService.deleteUserOrderById(orderId);
		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.OK.name())
						.code(HttpStatus.OK.value())
						.isError(false)
						.build())
				.payload(orderId)
				.build();

		return ResponseEntity.ok(response);
	}

	@ValidateUserId
	@GetMapping(ApiRoutes.ORDER_SUMMARY)
	public ResponseEntity<Response> findUserOrdersSummary(
			@RequestParam(name = ApiRoutes.ORDER_SUMMARY_PAGE_NUMBER) Integer pageNumber,
			@RequestParam(name = ApiRoutes.ORDER_SUMMARY_PAGE_SIZE) Integer pageSize,
			@PathVariable Long userId,
			HttpServletRequest request) {
		Page<OrderSummaryProjection> orderSummaryPage = orderService.findUserOrderSummary(userId, pageSize, pageNumber);

		OrderSummaryListDTO orders = new OrderSummaryListDTO(
				orderSummaryPage.getContent(),
				orderSummaryPage.getTotalPages(),
				orderSummaryPage.getPageable().getPageSize(),
				orderSummaryPage.getTotalElements(),
				orderSummaryPage.hasNext()
		);

		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.OK.name())
						.code(HttpStatus.OK.value())
						.isError(false)
						.build())
				.payload(orders)
				.build();

		return ResponseEntity.ok(response);
	}
}