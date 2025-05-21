package org.pizzeria.fabulosa.web.controllers.open.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.pizzeria.fabulosa.web.dto.api.ResponseDTO;
import org.pizzeria.fabulosa.web.dto.order.CreatedOrderDTO;
import org.pizzeria.fabulosa.web.dto.order.NewAnonOrderDTO;
import org.pizzeria.fabulosa.web.dto.user.RegisterDTO;
import org.springframework.http.ResponseEntity;

import static org.pizzeria.fabulosa.web.util.constant.ApiResponses.*;

@Tag(name = "Anonymous User API", description = "Anonymous user related operations")
public interface AnonControllerSwagger {

	@Operation(operationId = "registerAnonUser", summary = "Register anonymous user")
	@ApiResponse(
			responseCode = CREATED,
			description = "Registration successful",
			content = @Content())
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
	ResponseEntity<?> registerAnonUser(
			@RequestBody(
					required = true,
					content = @Content(
							mediaType = JSON,
							schema = @Schema(implementation = RegisterDTO.class)
					))
			@Valid RegisterDTO registerDTO,
			HttpServletRequest request);


	@Operation(operationId = "createAnonOrder", summary = "Create order as an anonymous user")
	@ApiResponse(
			responseCode = CREATED,
			description = "Returns created order",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = CreatedOrderDTO.class)))
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
	ResponseEntity<?> createAnonOrder(
			@RequestBody(
					required = true,
					content = @Content(mediaType = JSON, schema = @Schema(implementation = NewAnonOrderDTO.class)))
			@Valid NewAnonOrderDTO newAnonOrder,
			HttpServletRequest request);
}
