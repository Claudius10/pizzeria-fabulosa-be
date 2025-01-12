package org.pizzeria.fabulosa.web.dto.order.dto;

import org.pizzeria.fabulosa.web.dto.order.projection.OrderSummaryProjection;

import java.util.List;

public record OrderSummaryListDTO(

		List<OrderSummaryProjection> orderList,
		int totalPages,
		int pageSize,
		long totalElements,
		boolean hasNext
) {
}