package org.pizzeria.fabulosa.web.dto.order.dto;

import org.pizzeria.fabulosa.common.entity.address.Address;
import org.pizzeria.fabulosa.common.entity.cart.Cart;
import org.pizzeria.fabulosa.common.entity.order.OrderDetails;

public record CreatedOrderDTO(

		Long id,

		String formattedCreatedOn,

		CustomerDTO customer,

		Address address,

		OrderDetails orderDetails,

		Cart cart) {
}