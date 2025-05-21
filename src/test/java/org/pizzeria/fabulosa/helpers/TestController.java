package org.pizzeria.fabulosa.helpers;

import lombok.AllArgsConstructor;
import org.pizzeria.fabulosa.web.dto.api.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tests")
@AllArgsConstructor
class TestController {

	@GetMapping()
	ResponseEntity<ResponseDTO> testGetEndpoint() {
		return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.builder().build());
	}

	@PostMapping()
	ResponseEntity<ResponseDTO> testPostEndpoint() {
		return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.builder().build());
	}

	@PostMapping("/error")
	ResponseEntity<?> errorTestPostEndpoint() {
		throw new IllegalArgumentException("TestError");
	}

	@GetMapping("/admin")
	ResponseEntity<ResponseDTO> adminTestEndPoint() {
		return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.builder().build());
	}
}