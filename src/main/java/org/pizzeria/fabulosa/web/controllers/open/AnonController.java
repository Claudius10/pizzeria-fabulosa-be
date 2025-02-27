package org.pizzeria.fabulosa.web.controllers.open;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pizzeria.fabulosa.services.order.OrderService;
import org.pizzeria.fabulosa.services.user.UserService;
import org.pizzeria.fabulosa.web.constants.ApiRoutes;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.api.Status;
import org.pizzeria.fabulosa.web.dto.auth.RegisterDTO;
import org.pizzeria.fabulosa.web.dto.order.dto.CreatedOrderDTO;
import org.pizzeria.fabulosa.web.dto.order.dto.NewAnonOrderDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.ANON_BASE)
@Validated
@Slf4j
public class AnonController {

	private final UserService userService;

	private final OrderService orderService;

	@PostMapping(ApiRoutes.ANON_REGISTER)
	public ResponseEntity<Response> registerAnonUser(@RequestBody @Valid RegisterDTO registerDTO, HttpServletRequest request) {
		userService.createUser(registerDTO);

		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.CREATED.name())
						.code(HttpStatus.CREATED.value())
						.isError(false)
						.build())
				.build();

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping(ApiRoutes.ANON_ORDER)
	public ResponseEntity<Response> createAnonOrder(@RequestBody @Valid NewAnonOrderDTO newAnonOrder, HttpServletRequest request) {
		CreatedOrderDTO createdOrder = orderService.createAnonOrder(newAnonOrder);

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
}