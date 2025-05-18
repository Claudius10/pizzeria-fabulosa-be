package org.pizzeria.fabulosa.web.controllers.open.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.resource.OfferListDTO;
import org.pizzeria.fabulosa.web.dto.resource.ProductListDTO;
import org.pizzeria.fabulosa.web.dto.resource.StoreListDTO;
import org.pizzeria.fabulosa.web.util.constant.ApiRoutes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

import static org.pizzeria.fabulosa.web.util.constant.ApiResponses.*;

@Tag(name = "Resources API", description = "Pizzeria's resources")
public interface ResourceControllerSwagger {

	@Operation(operationId = "findAllProductsByType", summary = "Returns all products by type with pagination")
	@ApiResponse(
			responseCode = OK,
			description = "Returns product list",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = ProductListDTO.class))
	)
	@ApiResponse(
			responseCode = BAD_REQUEST,
			description = "Validation failed or invalid request",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = Response.class))
	)
	@ApiResponse(
			responseCode = INTERNAL_SERVER_ERROR,
			description = "Unexpected exception occurred",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = Response.class))
	)
	ResponseEntity<ProductListDTO> findAllProductsByType(
			@Parameter(required = true, description = "Type of the product") @RequestParam String type,
			@Parameter(required = true, description = "Page number starting at 0") @RequestParam(name = ApiRoutes.PAGE_NUMBER) Integer pageNumber,
			@Parameter(required = true, description = "Page size") @RequestParam(name = ApiRoutes.PAGE_SIZE) Integer pageSize);


	@Operation(operationId = "findAllStores", summary = "Returns all stores")
	@ApiResponse(
			responseCode = OK,
			description = "Returns store list",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = StoreListDTO.class))
	)
	@ApiResponse(
			responseCode = INTERNAL_SERVER_ERROR,
			description = "Unexpected exception occurred",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = Response.class))
	)
	ResponseEntity<StoreListDTO> findAllStores();


	@Operation(operationId = "findAllOffers", summary = "Returns all offers")
	@ApiResponse(
			responseCode = OK,
			description = "Returns offer list",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = OfferListDTO.class))
	)
	@ApiResponse(
			responseCode = INTERNAL_SERVER_ERROR,
			description = "Unexpected exception occurred",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = Response.class))
	)
	ResponseEntity<OfferListDTO> findAllOffers();


	@Operation(operationId = "getNowAccountingDST", summary = "Returns the local date and time accounting for DST")
	@ApiResponse(
			responseCode = OK,
			description = "Returns the local date and time accounting for DST",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = LocalDateTime.class))
	)
	@ApiResponse(
			responseCode = INTERNAL_SERVER_ERROR,
			description = "Unexpected exception occurred",
			content = @Content(mediaType = JSON, schema = @Schema(implementation = Response.class))
	)
	ResponseEntity<LocalDateTime> getNowAccountingDST();
}
