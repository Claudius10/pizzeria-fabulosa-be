package org.pizzeria.fabulosa.web.dto.order;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record OrderDTO(
		@NotNull
		Long id,

		@NotNull
		LocalDateTime createdOn,

		@NotNull
		String formattedCreatedOn,

		@NotNull
		AddressDTO address,

		@NotNull
		OrderDetailsDTO orderDetails,

		@NotNull
		CartDTO cart
) {
}
