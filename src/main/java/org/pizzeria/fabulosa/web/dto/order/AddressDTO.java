package org.pizzeria.fabulosa.web.dto.order;

import org.pizzeria.fabulosa.web.validation.constraints.annotation.ValidAddress;

@ValidAddress
public record AddressDTO(

		Long id,

		String street,

		Integer number,

		String details
) {
}
