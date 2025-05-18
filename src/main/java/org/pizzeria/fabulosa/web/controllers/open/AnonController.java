package org.pizzeria.fabulosa.web.controllers.open;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pizzeria.fabulosa.web.dto.order.CreatedOrderDTO;
import org.pizzeria.fabulosa.web.controllers.open.swagger.AnonControllerSwagger;
import org.pizzeria.fabulosa.web.dto.order.NewAnonOrderDTO;
import org.pizzeria.fabulosa.web.dto.user.RegisterDTO;
import org.pizzeria.fabulosa.web.service.order.OrderService;
import org.pizzeria.fabulosa.web.service.user.UserService;
import org.pizzeria.fabulosa.web.util.constant.ApiResponses;
import org.pizzeria.fabulosa.web.util.constant.ApiRoutes;
import org.pizzeria.fabulosa.web.validation.order.CompositeValidator;
import org.pizzeria.fabulosa.web.validation.order.OrderValidatorInput;
import org.pizzeria.fabulosa.web.validation.order.ValidationResult;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.pizzeria.fabulosa.web.util.ResponseUtils.error;

@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.ANON_BASE)
@Slf4j
public class AnonController implements AnonControllerSwagger {

	private final UserService userService;

	private final OrderService orderService;

	private final CompositeValidator<OrderValidatorInput> newOrderValidator;

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
	public ResponseEntity<?> createAnonOrder(@RequestBody @Valid NewAnonOrderDTO newAnonOrder, HttpServletRequest request) {

		Optional<ValidationResult> validate = newOrderValidator.validate(new OrderValidatorInput(newAnonOrder.cart(), newAnonOrder.orderDetails()));

		if (validate.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error(this.getClass().getSimpleName(), validate.get().message(), request.getPathInfo()));
		}

		CreatedOrderDTO createdOrder = orderService.createAnonOrder(newAnonOrder);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
	}
}