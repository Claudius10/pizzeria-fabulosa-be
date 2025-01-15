package org.pizzeria.fabulosa.web.dto.order.dto;

import org.pizzeria.fabulosa.entity.address.Address;
import org.pizzeria.fabulosa.entity.cart.Cart;
import org.pizzeria.fabulosa.entity.order.OrderDetails;

public record CreatedOrderDTO(

		Long id,

		String formattedCreatedOn,

		CustomerDTO customer,

		Address address,

		OrderDetails orderDetails,

		Cart cart) {
}