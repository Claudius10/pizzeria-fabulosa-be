package org.pizzeria.fabulosa.web.controllers.user.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.pizzeria.fabulosa.web.dto.api.ResponseDTO;
import org.pizzeria.fabulosa.web.dto.order.AddressDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import static org.pizzeria.fabulosa.web.util.constant.ApiResponses.*;

@Tag(name = "User Address API", description = "User address related operations")
@SecurityRequirement(name = "Bearer_Authentication")
public interface UserAddressControllerSwagger {

	@Operation(operationId = "findUserAddressListById", summary = "Find user's address list")
	@ApiResponse(
			responseCode = OK,
			description = "Returns user addresses JSON array",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = AddressDTO[].class))
	)
	@ApiResponse(
			responseCode = NO_CONTENT,
			description = "User address list is empty",
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
			description = "Unexpected exception occurred",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = ResponseDTO.class))
	)
	ResponseEntity<?> findUserAddressListById(
			@Parameter(required = true, description = "Id of the user for which to find the address list") @PathVariable Long userId,
			HttpServletRequest request);

	@Operation(operationId = "createUserAddress", summary = "Create user address")
	@ApiResponse(
			responseCode = CREATED,
			description = "Address created",
			content = @Content()
	)
	@ApiResponse(
			responseCode = BAD_REQUEST,
			description = "Validation failed or user address limit of 3 has been reached",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = ResponseDTO.class))
	)
	@ApiResponse(
			responseCode = UNAUTHORIZED,
			description = "User authentification failed",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = ResponseDTO.class))
	)
	@ApiResponse(
			responseCode = INTERNAL_SERVER_ERROR,
			description = "Unexpected exception occurred",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = ResponseDTO.class))
	)
	ResponseEntity<?> createUserAddress(
			@RequestBody(
					required = true,
					content = @Content(
							mediaType = JSON,
							schema = @Schema(implementation = AddressDTO.class)
					))
			@Valid AddressDTO address,
			@Parameter(required = true, description = "Id of the user for which to create the address") @PathVariable Long userId,
			HttpServletRequest request);

	@Operation(operationId = "deleteUserAddress", summary = "Delete user address")
	@ApiResponse(
			responseCode = OK,
			description = "Address deleted",
			content = @Content()
	)
	@ApiResponse(
			responseCode = BAD_REQUEST,
			description = "Validation failed or address to delete is not part of user address list",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = ResponseDTO.class))
	)
	@ApiResponse(
			responseCode = UNAUTHORIZED,
			description = "User authentification failed",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = ResponseDTO.class))
	)
	@ApiResponse(
			responseCode = INTERNAL_SERVER_ERROR,
			description = "Unexpected exception occurred",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = ResponseDTO.class))
	)
	ResponseEntity<?> deleteUserAddress(
			@Parameter(required = true, description = "Id of the address to delete") @PathVariable Long addressId,
			@Parameter(required = true, description = "Id of the user the address belongs to") @PathVariable Long userId,
			HttpServletRequest request);
}
