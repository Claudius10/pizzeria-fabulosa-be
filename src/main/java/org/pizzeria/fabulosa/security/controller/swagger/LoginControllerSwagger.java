package org.pizzeria.fabulosa.security.controller.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pizzeria.fabulosa.web.dto.api.ResponseDTO;
import org.springframework.web.bind.annotation.RequestParam;

import static org.pizzeria.fabulosa.web.util.constant.ApiResponses.*;

@Tag(name = "Login API", description = "Acquire JWT tokens")
public interface LoginControllerSwagger {

	@Operation(operationId = "login")
	@ApiResponse(
			responseCode = OK,
			description = "Returns ACCESS_TOKEN and ID_TOKEN cookies",
			content = @Content(mediaType = TEXT)
	)
	@ApiResponse(
			responseCode = BAD_REQUEST,
			description = "Validation failed or invalid request",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = ResponseDTO.class))
	)
	@ApiResponse(
			responseCode = INTERNAL_SERVER_ERROR,
			description = "Unexpected exception occurred",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = ResponseDTO.class))
	)
	void login(
			@Parameter(required = true, description = "Email") @RequestParam String username,
			@Parameter(required = true, description = "Password") @RequestParam String password
	);
}
