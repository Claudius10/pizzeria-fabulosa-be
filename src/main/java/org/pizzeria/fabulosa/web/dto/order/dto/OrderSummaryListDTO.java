package org.pizzeria.fabulosa.web.dto.order.dto;

import java.util.List;

public record OrderSummaryListDTO(

		List<OrderSummaryDTO> orderList,
		int totalPages,
		int pageSize,
		long totalElements,
		boolean hasNext
) {
}