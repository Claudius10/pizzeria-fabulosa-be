package org.pizzeria.fabulosa.web.dto.order.dto;

import org.pizzeria.fabulosa.entity.address.Address;
import org.pizzeria.fabulosa.entity.cart.Cart;
import org.pizzeria.fabulosa.entity.order.OrderDetails;
import org.pizzeria.fabulosa.utils.enums.OrderState;

public record CreatedOrderDTO(

		Long id,

		String formattedCreatedOn,

		OrderState state,

		CustomerDTO customer,

		Address address,

		OrderDetails orderDetails,

		Cart cart) {
}