package org.pizzeria.fabulosa.web.controllers.open;

import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.common.entity.resources.Product;
import org.pizzeria.fabulosa.common.util.TimeUtils;
import org.pizzeria.fabulosa.web.controllers.open.swagger.ResourceControllerSwagger;
import org.pizzeria.fabulosa.web.dto.resource.OfferListDTO;
import org.pizzeria.fabulosa.web.dto.resource.ProductListDTO;
import org.pizzeria.fabulosa.web.dto.resource.StoreListDTO;
import org.pizzeria.fabulosa.web.service.resources.ResourceService;
import org.pizzeria.fabulosa.web.util.constant.ApiRoutes;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.RESOURCE_BASE)
public class ResourceController implements ResourceControllerSwagger {

	private final ResourceService resourceService;

	@GetMapping(path = ApiRoutes.RESOURCE_PRODUCT, params = ApiRoutes.RESOURCE_PRODUCT_PARAM)
	public ResponseEntity<ProductListDTO> findAllProductsByType(
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

		return ResponseEntity.ok(productListDTO);
	}

	@GetMapping(ApiRoutes.RESOURCE_STORE)
	public ResponseEntity<StoreListDTO> findAllStores() {
		return ResponseEntity.ok(new StoreListDTO(resourceService.findAllStores()));
	}

	@GetMapping(ApiRoutes.RESOURCE_OFFER)
	public ResponseEntity<OfferListDTO> findAllOffers() {
		return ResponseEntity.ok(new OfferListDTO(resourceService.findAllOffers()));
	}

	@GetMapping(ApiRoutes.LOCAL_DATE_TIME_NOW)
	public ResponseEntity<LocalDateTime> getNowAccountingDST() {
		return ResponseEntity.ok(TimeUtils.getNowAccountingDST());
	}
}