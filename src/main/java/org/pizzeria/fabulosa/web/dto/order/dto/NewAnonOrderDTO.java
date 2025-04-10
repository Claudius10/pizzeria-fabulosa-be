package org.pizzeria.fabulosa.web.dto.order.dto;

import jakarta.validation.Valid;
import org.pizzeria.fabulosa.entity.address.Address;
import org.pizzeria.fabulosa.entity.cart.Cart;
import org.pizzeria.fabulosa.entity.order.OrderDetails;

public record NewAnonOrderDTO(

		@Valid
		CustomerDTO customer,

		@Valid
		Address address,

		@Valid
		OrderDetails orderDetails,

		Cart cart
) {
}