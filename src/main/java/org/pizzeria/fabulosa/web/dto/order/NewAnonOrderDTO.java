package org.pizzeria.fabulosa.web.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record NewAnonOrderDTO(

		@NotNull
		@Valid
		CustomerDTO customer,

		@NotNull
		@Valid
		AddressDTO address,

		@NotNull
		@Valid
		OrderDetailsDTO orderDetails,

		@NotNull
		@Valid
		CartDTO cart
) {
}