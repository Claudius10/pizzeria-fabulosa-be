package org.pizzeria.fabulosa.web.dto.order;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderSummaryListDTO(

		@NotNull
		List<OrderSummaryDTO> orderList,

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