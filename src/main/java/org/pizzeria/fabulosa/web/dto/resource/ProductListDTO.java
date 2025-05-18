package org.pizzeria.fabulosa.web.dto.resource;

import jakarta.validation.constraints.NotNull;
import org.pizzeria.fabulosa.common.entity.resources.Product;

import java.util.List;

public record ProductListDTO(
		@NotNull
		List<Product> productList,

		@NotNull
		int totalPages,

		@NotNull
		int pageSize,

		@NotNull
		long totalElements,

		@NotNull
		boolean hasNext
) {
}