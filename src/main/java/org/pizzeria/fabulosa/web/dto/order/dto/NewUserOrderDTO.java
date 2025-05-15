package org.pizzeria.fabulosa.web.dto.order.dto;

import jakarta.validation.constraints.NotNull;
import org.pizzeria.fabulosa.common.entity.cart.Cart;
import org.pizzeria.fabulosa.common.entity.order.OrderDetails;

public record NewUserOrderDTO(
		@NotNull
		Long addressId,

		OrderDetails orderDetails,

		Cart cart) {
}