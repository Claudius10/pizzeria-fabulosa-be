package org.pizzeria.fabulosa.web.dto.resource;

import org.pizzeria.fabulosa.common.entity.resources.Product;

import java.util.List;

public record ProductListDTO(
		List<Product> productList,
		int totalPages,
		int pageSize,
		long totalElements,
		boolean hasNext
) {
}