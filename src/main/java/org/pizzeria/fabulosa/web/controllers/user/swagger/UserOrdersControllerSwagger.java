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
import org.pizzeria.fabulosa.web.dto.order.CreatedOrderDTO;
import org.pizzeria.fabulosa.web.dto.order.OrderSummaryListDTO;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.order.NewUserOrderDTO;
import org.pizzeria.fabulosa.web.dto.order.UserOrderDTO;
import org.pizzeria.fabulosa.web.util.constant.ApiRoutes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import static org.pizzeria.fabulosa.web.util.constant.ApiResponses.*;

@Tag(name = "User orders API")
@SecurityRequirement(name = "Bearer_Authentication")
public interface UserOrdersControllerSwagger {

	@Operation(operationId = "createUserOrder", summary = "Create user order")
	@ApiResponse(
			responseCode = CREATED,
			description = "Returns created order",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = CreatedOrderDTO.class))
	)
	@ApiResponse(
			responseCode = BAD_REQUEST,
			description = "Validation failed or invalid request",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = Response.class))
	)
	@ApiResponse(
			responseCode = UNAUTHORIZED,
			description = "User authentification failed",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = Response.class))
	)
	@ApiResponse(
			responseCode = INTERNAL_SERVER_ERROR,
			description = "Unexpected exception occurred",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = Response.class))
	)
	ResponseEntity<?> createUserOrder(
			@RequestBody(
					required = true,
					content = @Content(
							mediaType = JSON,
							schema = @Schema(implementation = NewUserOrderDTO.class)
					))
			@Valid NewUserOrderDTO order,
			@Parameter(required = true, description = "Id of the user for which to create the order") @PathVariable Long userId,
			HttpServletRequest request);


	@Operation(operationId = "findUserOrderDTO", summary = "Find user order by id")
	@ApiResponse(
			responseCode = OK,
			description = "Returns user order",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = UserOrderDTO.class))
	)
	@ApiResponse(
			responseCode = NO_CONTENT,
			description = "User order not found",
			content = @Content()
	)
	@ApiResponse(
			responseCode = BAD_REQUEST,
			description = "Validation failed or invalid request",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = Response.class))
	)
	@ApiResponse(
			responseCode = UNAUTHORIZED,
			description = "User authentification failed",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = Response.class))
	)
	@ApiResponse(
			responseCode = INTERNAL_SERVER_ERROR,
			description = "Unexpected exception occurred",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = Response.class))
	)
	ResponseEntity<?> findUserOrderDTO(
			@Parameter(required = true, description = "Id of the order to find") @PathVariable Long orderId,
			@Parameter(required = true, description = "Id of the user the order belongs to") @PathVariable Long userId,
			HttpServletRequest request);

	@Operation(operationId = "deleteUserOrderById", summary = "Delete user order by id")
	@ApiResponse(
			responseCode = OK,
			description = "Returns the deleted order's id",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = Long.class))
	)
	@ApiResponse(
			responseCode = NO_CONTENT,
			description = "User order not found",
			content = @Content()
	)
	@ApiResponse(
			responseCode = BAD_REQUEST,
			description = "Validation failed or invalid request or order delete time-limit passed",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = Response.class))
	)
	@ApiResponse(
			responseCode = UNAUTHORIZED,
			description = "User authentification failed",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = Response.class))
	)
	@ApiResponse(
			responseCode = INTERNAL_SERVER_ERROR,
			description = "Unexpected exception occurred",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = Response.class))
	)
	ResponseEntity<?> deleteUserOrderById(
			@Parameter(required = true, description = "Id of the order to delete") @PathVariable Long orderId,
			@Parameter(required = true, description = "Id of the user the order belongs to") @PathVariable Long userId,
			HttpServletRequest request);


	@Operation(operationId = "findUserOrdersSummary", summary = "Returns user orders summary with pagination")
	@ApiResponse(
			responseCode = OK,
			description = "Returns user orders summary",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = OrderSummaryListDTO.class))
	)
	@ApiResponse(
			responseCode = NO_CONTENT,
			description = "User orders summary is empty",
			content = @Content()
	)
	@ApiResponse(
			responseCode = BAD_REQUEST,
			description = "Validation failed or invalid request",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = Response.class))
	)
	@ApiResponse(
			responseCode = UNAUTHORIZED,
			description = "User authentification failed",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = Response.class))
	)
	@ApiResponse(
			responseCode = INTERNAL_SERVER_ERROR,
			description = "Unexpected exception occurred",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = Response.class))
	)
	ResponseEntity<?> findUserOrdersSummary(
			@Parameter(required = true, description = "Page number starting at 0") @RequestParam(name = ApiRoutes.PAGE_NUMBER) Integer pageNumber,
			@Parameter(required = true, description = "Page size") @RequestParam(name = ApiRoutes.PAGE_SIZE) Integer pageSize,
			@Parameter(required = true, description = "Id of the user for which to find the order summary") @PathVariable Long userId,
			HttpServletRequest request);
}
