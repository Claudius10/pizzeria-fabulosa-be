package org.pizzeria.fabulosa.common.entity.dto;

public record OrderSummaryDTO(
		Long id,
		String formattedCreatedOn,
		String paymentMethod,
		Integer quantity,
		Double cost,
		Double costAfterOffers
) {
}
