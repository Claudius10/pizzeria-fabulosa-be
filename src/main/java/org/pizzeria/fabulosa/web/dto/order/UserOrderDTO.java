package org.pizzeria.fabulosa.web.dto.order;

import java.time.LocalDateTime;

public record UserOrderDTO(
		Long id,
		LocalDateTime createdOn,
		String formattedCreatedOn,
		AddressDTO address,
		OrderDetailsDTO orderDetails,
		CartDTO cart
) {
}
