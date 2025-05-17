package org.pizzeria.fabulosa.helpers;

import lombok.AllArgsConstructor;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.order.NewUserOrderDTO;
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
		return ResponseEntity.status(HttpStatus.OK).body(Response.builder().build());
	}

	@PostMapping()
	ResponseEntity<Response> testPostEndpoint() {
		return ResponseEntity.status(HttpStatus.OK).body(Response.builder().build());
	}

	@PostMapping("/error")
	ResponseEntity<?> errorTestPostEndpoint() {
		throw new IllegalArgumentException("TestError");
	}

	@GetMapping("/admin")
	ResponseEntity<Response> adminTestEndPoint() {
		return ResponseEntity.status(HttpStatus.OK).body(Response.builder().build());
	}

	@PostMapping(path = "/user/{userId}/order", params = "minusMin")
	public ResponseEntity<Long> createOrderTestSubjects(@RequestBody NewUserOrderDTO order, @PathVariable Long userId, @RequestParam Long minusMin) {
		LocalDateTime createdOn = LocalDateTime.now().minusMinutes(minusMin);
		return ResponseEntity.status(HttpStatus.OK).body(orderServiceImplTest.createOrderTestSubjects(order, userId, createdOn));
	}
}