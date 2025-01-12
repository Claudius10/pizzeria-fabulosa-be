package org.pizzeria.fabulosa.web.dto.order.dto;

import jakarta.validation.constraints.NotNull;
import org.pizzeria.fabulosa.entity.cart.Cart;
import org.pizzeria.fabulosa.entity.order.OrderDetails;

import java.time.LocalDateTime;

public record UpdateUserOrderDTO(
		@NotNull(message = "El valor del ID de la dirección no puede ser null.")
		Long addressId,

		@NotNull(message = "El valor de la fecha de creación del pedido no puede ser null.")
		LocalDateTime createdOn,

		@NotNull(message = "Los detalles del pedido (orderDetails) no pueden ser null.")
		OrderDetails orderDetails,

		Cart cart
) {

	public UpdateUserOrderDTO withCart(Cart cart) {
		return new UpdateUserOrderDTO(addressId(), createdOn(), orderDetails(), cart);
	}

}