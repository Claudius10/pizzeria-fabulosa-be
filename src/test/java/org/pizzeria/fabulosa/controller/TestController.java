package org.pizzeria.fabulosa.controller;

import lombok.AllArgsConstructor;
import org.pizzeria.fabulosa.order.context.OrderServiceImplTest;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.api.Status;
import org.pizzeria.fabulosa.web.dto.order.dto.NewUserOrderDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/tests")
@AllArgsConstructor
class TestController {

	private final OrderServiceImplTest orderServiceImplTest;

	@GetMapping()
	ResponseEntity<Response> testGetEndpoint() {
		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.OK.name())
						.code(HttpStatus.OK.value())
						.isError(false)
						.build())
				.build();

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping()
	ResponseEntity<Response> testPostEndpoint() {
		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.OK.name())
						.code(HttpStatus.OK.value())
						.isError(false)
						.build())
				.build();

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/error")
	ResponseEntity<?> errorTestPostEndpoint() {
		throw new IllegalArgumentException("TestError");
	}

	@GetMapping("/admin")
	ResponseEntity<Response> adminTestEndPoint() {
		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.OK.name())
						.code(HttpStatus.OK.value())
						.isError(false)
						.build())
				.build();

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping(path = "/user/{userId}/order", params = "minusMin")
	public ResponseEntity<Long> createOrderTestSubjects(@RequestBody NewUserOrderDTO order, @PathVariable Long userId, @RequestParam Long minusMin) {
		LocalDateTime createdOn = LocalDateTime.now().minusMinutes(minusMin);
		return ResponseEntity.status(HttpStatus.OK).body(orderServiceImplTest.createOrderTestSubjects(order, userId, createdOn));
	}
}