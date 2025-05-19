package org.pizzeria.fabulosa.web.controllers.user.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pizzeria.fabulosa.web.dto.api.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import static org.pizzeria.fabulosa.web.util.constant.ApiResponses.*;
import static org.pizzeria.fabulosa.web.util.constant.SecurityConstants.ACCESS_TOKEN;

@Tag(name = "User account API", description = "User account related operations")
@SecurityRequirement(name = ACCESS_TOKEN)
public interface UserControllerSwagger {

	@Operation(operationId = "deleteUser", summary = "Delete user account")
	@ApiResponse(
			responseCode = OK,
			description = "Account deleted",
			content = @Content()
	)
	@ApiResponse(
			responseCode = BAD_REQUEST,
			description = "Validation failed or invalid request",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = ResponseDTO.class))
	)
	@ApiResponse(
			responseCode = UNAUTHORIZED,
			description = "User authentification failed",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = ResponseDTO.class))
	)
	@ApiResponse(
			responseCode = INTERNAL_SERVER_ERROR,
			description = "Unexpected exception occurred or attempted to delete dummy account",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = ResponseDTO.class))
	)
	ResponseEntity<?> deleteUser(
			@RequestParam @Parameter(required = true, description = "Id of the user account to delete") Long id,
			@RequestParam @Parameter(required = true, description = "Password of the user account to delete") String password,
			HttpServletRequest request,
			HttpServletResponse response);
}
