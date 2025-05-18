package org.pizzeria.fabulosa.web.dto.order;

import jakarta.validation.constraints.NotNull;

public record CreatedOrderDTO(

		@NotNull
		Long id,

		@NotNull
		String formattedCreatedOn,

		@NotNull
		CustomerDTO customer,

		@NotNull
		AddressDTO address,

		@NotNull
		OrderDetailsDTO orderDetails,

		@NotNull
		CartDTO cart) {
}