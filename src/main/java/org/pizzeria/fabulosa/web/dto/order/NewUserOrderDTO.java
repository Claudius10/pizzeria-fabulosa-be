package org.pizzeria.fabulosa.web.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record NewUserOrderDTO(

		@NotNull
		Long addressId,

		@NotNull
		@Valid
		OrderDetailsDTO orderDetails,

		@NotNull
		@Valid
		CartDTO cart) {
}