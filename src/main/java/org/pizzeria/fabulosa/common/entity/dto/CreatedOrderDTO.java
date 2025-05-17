package org.pizzeria.fabulosa.common.entity.dto;

import org.pizzeria.fabulosa.web.dto.order.AddressDTO;
import org.pizzeria.fabulosa.web.dto.order.CartDTO;
import org.pizzeria.fabulosa.web.dto.order.CustomerDTO;
import org.pizzeria.fabulosa.web.dto.order.OrderDetailsDTO;

public record CreatedOrderDTO(

		Long id,

		String formattedCreatedOn,

		CustomerDTO customer,

		AddressDTO address,

		OrderDetailsDTO orderDetails,

		CartDTO cart) {
}