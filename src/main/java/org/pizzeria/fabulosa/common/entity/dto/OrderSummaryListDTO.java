package org.pizzeria.fabulosa.common.entity.dto;

import java.util.List;

public record OrderSummaryListDTO(

		List<OrderSummaryDTO> orderList,
		int totalPages,
		int pageSize,
		long totalElements,
		boolean hasNext
) {
}