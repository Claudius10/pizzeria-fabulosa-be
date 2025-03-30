package org.pizzeria.fabulosa.web.controllers.open;

import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.entity.resources.Product;
import org.pizzeria.fabulosa.services.resources.ResourceService;
import org.pizzeria.fabulosa.utils.TimeUtils;
import org.pizzeria.fabulosa.web.constants.ApiRoutes;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.api.Status;
import org.pizzeria.fabulosa.web.dto.resource.ProductListDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.RESOURCE_BASE)
public class ResourceController {

	private final ResourceService resourceService;

	@GetMapping(path = ApiRoutes.RESOURCE_PRODUCT, params = ApiRoutes.RESOURCE_PRODUCT_PARAM)
	public ResponseEntity<Response> findAllProductsByType(
			@RequestParam String type,
			@RequestParam(name = ApiRoutes.PAGE_NUMBER) Integer pageNumber,
			@RequestParam(name = ApiRoutes.PAGE_SIZE) Integer pageSize) {

		Page<Product> allProductsByType = resourceService.findAllProductsByType(type, pageSize, pageNumber);

		ProductListDTO productListDTO = new ProductListDTO(
				allProductsByType.getContent(),
				allProductsByType.getTotalPages(),
				allProductsByType.getPageable().getPageSize(),
				allProductsByType.getTotalElements(),
				allProductsByType.hasNext()
		);

		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.OK.name())
						.code(HttpStatus.OK.value())
						.isError(false)
						.build())
				.payload(productListDTO)
				.build();

		return ResponseEntity.ok(response);
	}

	@GetMapping(ApiRoutes.RESOURCE_STORE)
	public ResponseEntity<Response> findAllStores() {

		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.OK.name())
						.code(HttpStatus.OK.value())
						.isError(false)
						.build())
				.payload(resourceService.findAllStores())
				.build();

		return ResponseEntity.ok(response);
	}

	@GetMapping(ApiRoutes.RESOURCE_OFFER)
	public ResponseEntity<Response> findAllOffers() {

		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.OK.name())
						.code(HttpStatus.OK.value())
						.isError(false)
						.build())
				.payload(resourceService.findAllOffers())
				.build();

		return ResponseEntity.ok(response);
	}

	@GetMapping(ApiRoutes.LOCAL_DATE_TIME_NOW)
	public ResponseEntity<Response> getNowAccountingDST() {

		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.OK.name())
						.code(HttpStatus.OK.value())
						.isError(false)
						.build())
				.payload(TimeUtils.getNowAccountingDST())
				.build();

		return ResponseEntity.ok(response);
	}
}