package org.pizzeria.fabulosa.web.controllers.open;

import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.common.entity.resources.Product;
import org.pizzeria.fabulosa.common.util.TimeUtils;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.resource.ProductListDTO;
import org.pizzeria.fabulosa.web.service.resources.ResourceService;
import org.pizzeria.fabulosa.web.util.constant.ApiRoutes;
import org.springframework.data.domain.Page;
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

		return ResponseEntity.ok(Response.builder().payload(productListDTO).build());
	}

	@GetMapping(ApiRoutes.RESOURCE_STORE)
	public ResponseEntity<Response> findAllStores() {
		return ResponseEntity.ok(Response.builder().payload(resourceService.findAllStores()).build());
	}

	@GetMapping(ApiRoutes.RESOURCE_OFFER)
	public ResponseEntity<Response> findAllOffers() {
		return ResponseEntity.ok(Response.builder().payload(resourceService.findAllOffers()).build());
	}

	@GetMapping(ApiRoutes.LOCAL_DATE_TIME_NOW)
	public ResponseEntity<Response> getNowAccountingDST() {
		return ResponseEntity.ok(Response.builder().payload(TimeUtils.getNowAccountingDST()).build());
	}
}