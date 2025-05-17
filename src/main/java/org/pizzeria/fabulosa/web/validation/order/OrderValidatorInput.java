package org.pizzeria.fabulosa.web.validation.order;

import org.pizzeria.fabulosa.web.dto.order.CartDTO;
import org.pizzeria.fabulosa.web.dto.order.OrderDetailsDTO;

public record OrderValidatorInput(
		CartDTO cart,
		OrderDetailsDTO orderDetails
) {
}
