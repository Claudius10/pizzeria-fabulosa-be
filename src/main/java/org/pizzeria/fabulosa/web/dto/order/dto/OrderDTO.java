package org.pizzeria.fabulosa.web.dto.order.dto;

import org.pizzeria.fabulosa.entity.address.Address;
import org.pizzeria.fabulosa.entity.cart.Cart;
import org.pizzeria.fabulosa.entity.order.OrderDetails;

import java.time.LocalDateTime;

public record OrderDTO(
		Long id,
		LocalDateTime createdOn,
		LocalDateTime updatedOn,
		String formattedCreatedOn,
		String formattedUpdatedOn,
		Address address,
		OrderDetails orderDetails,
		Cart cart
) {
}