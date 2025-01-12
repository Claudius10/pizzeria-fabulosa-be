package org.pizzeria.fabulosa.controller;

import lombok.AllArgsConstructor;
import org.pizzeria.fabulosa.entity.error.Error;
import org.pizzeria.fabulosa.order.context.OrderServiceImplTest;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.api.Status;
import org.pizzeria.fabulosa.web.dto.order.dto.NewUserOrderDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

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
		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.BAD_REQUEST.name())
						.code(HttpStatus.BAD_REQUEST.value())
						.isError(true)
						.build())
				.build();

		response.setError(Error.builder()
				.id(UUID.randomUUID().getMostSignificantBits())
				.cause("TestError")
				.origin(TestController.class.getSimpleName() + ".errorTestPostEndpoint")
				.path("/api/tests/error")
				.logged(false)
				.fatal(false).build());

		return ResponseEntity.status(HttpStatus.OK).body(response);
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