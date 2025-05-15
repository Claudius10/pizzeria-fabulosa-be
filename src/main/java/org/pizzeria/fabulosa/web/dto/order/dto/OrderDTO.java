package org.pizzeria.fabulosa.web.dto.order.dto;

import org.pizzeria.fabulosa.common.entity.address.Address;
import org.pizzeria.fabulosa.common.entity.cart.Cart;
import org.pizzeria.fabulosa.common.entity.order.OrderDetails;

import java.time.LocalDateTime;

public record OrderDTO(
		Long id,
		LocalDateTime createdOn,
		String formattedCreatedOn,
		Address address,
		OrderDetails orderDetails,
		Cart cart
) {
}