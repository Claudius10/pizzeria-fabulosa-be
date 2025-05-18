package org.pizzeria.fabulosa.web.dto.order;

import jakarta.validation.constraints.NotNull;
import org.pizzeria.fabulosa.web.validation.constraints.annotation.ValidAddress;

@ValidAddress
public record AddressDTO(

		Long id,

		@NotNull
		String street,

		@NotNull
		Integer number,

		String details
) {
}
