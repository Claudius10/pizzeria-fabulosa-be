package org.pizzeria.fabulosa.web.dto.order.dto;

public record OrderSummaryDTO(
		Long id,
		String formattedCreatedOn,
		String paymentMethod,
		Integer quantity,
		Double cost,
		Double costAfterOffers
) {
}
