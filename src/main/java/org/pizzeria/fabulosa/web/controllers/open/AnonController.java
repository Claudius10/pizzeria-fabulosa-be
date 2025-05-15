package org.pizzeria.fabulosa.web.controllers.open;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.order.dto.CreatedOrderDTO;
import org.pizzeria.fabulosa.web.dto.order.dto.NewAnonOrderDTO;
import org.pizzeria.fabulosa.web.dto.user.dto.RegisterDTO;
import org.pizzeria.fabulosa.web.order.validation.OrderCartValidator;
import org.pizzeria.fabulosa.web.order.validation.OrderDetailsValidator;
import org.pizzeria.fabulosa.web.order.validation.OrderValidationResult;
import org.pizzeria.fabulosa.web.service.order.OrderService;
import org.pizzeria.fabulosa.web.service.user.UserService;
import org.pizzeria.fabulosa.web.util.constant.ApiResponses;
import org.pizzeria.fabulosa.web.util.constant.ApiRoutes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.pizzeria.fabulosa.web.util.ResponseUtils.error;

@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.ANON_BASE)
@Validated
@Slf4j
public class AnonController {

	private final UserService userService;

	private final OrderService orderService;

	@PostMapping(ApiRoutes.ANON_REGISTER)
	public ResponseEntity<?> registerAnonUser(@RequestBody @Valid RegisterDTO registerDTO, HttpServletRequest request) {
		try {
			userService.createUser(registerDTO);
		} catch (DataIntegrityViolationException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error(this.getClass().getSimpleName(), ApiResponses.USER_EMAIL_ALREADY_EXISTS, request.getPathInfo()));
		}
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping(ApiRoutes.ANON_ORDER)
	public ResponseEntity<Response> createAnonOrder(@RequestBody @Valid NewAnonOrderDTO newAnonOrder, HttpServletRequest request) {

		OrderValidationResult cart = OrderCartValidator.validate(newAnonOrder.cart());
		if (!cart.isValid()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error(this.getClass().getSimpleName(), cart.getMessage(), request.getPathInfo()));
		}

		OrderValidationResult orderDetails = OrderDetailsValidator.validate(newAnonOrder.cart(), newAnonOrder.orderDetails());
		if (!orderDetails.isValid()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error(this.getClass().getSimpleName(), orderDetails.getMessage(), request.getPathInfo()));
		}

		CreatedOrderDTO createdOrder = orderService.createAnonOrder(newAnonOrder);
		return ResponseEntity.status(HttpStatus.CREATED).body(Response.builder().payload(createdOrder).build());
	}
}