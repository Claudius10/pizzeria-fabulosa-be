package org.pizzeria.fabulosa.controller;

import lombok.AllArgsConstructor;
import org.pizzeria.fabulosa.order.context.OrderServiceImplTest;
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
	ResponseEntity<?> testGetEndpoint() {
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PostMapping()
	ResponseEntity<?> testPostEndpoint() {
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PostMapping("/error")
	ResponseEntity<?> errorTestPostEndpoint() {
		throw new IllegalArgumentException("TestError");
	}

	@GetMapping("/admin")
	ResponseEntity<?> adminTestEndPoint() {
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PostMapping(path = "/user/{userId}/order", params = "minusMin")
	public ResponseEntity<Long> createOrderTestSubjects(@RequestBody NewUserOrderDTO order, @PathVariable Long userId, @RequestParam Long minusMin) {
		LocalDateTime createdOn = LocalDateTime.now().minusMinutes(minusMin);
		return ResponseEntity.status(HttpStatus.OK).body(orderServiceImplTest.createOrderTestSubjects(order, userId, createdOn));
	}
}