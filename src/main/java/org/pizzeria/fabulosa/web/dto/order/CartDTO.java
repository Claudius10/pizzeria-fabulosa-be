package org.pizzeria.fabulosa.web.dto.order;

import org.pizzeria.fabulosa.web.validation.constraints.annotation.DoubleLength;
import org.pizzeria.fabulosa.web.validation.constraints.annotation.DoubleLengthNullable;
import org.pizzeria.fabulosa.web.validation.constraints.annotation.IntegerLength;

import java.util.List;

import static org.pizzeria.fabulosa.web.util.constant.ValidationResponses.*;

public record CartDTO(

		@IntegerLength(min = 1, max = 2, message = CART_MAX_PRODUCTS_QUANTITY_ERROR)
		Integer totalQuantity,

		@DoubleLength(min = 1, max = 6, message = TOTAL_COST_ERROR)
		Double totalCost,

		@DoubleLengthNullable(min = 0, max = 6, message = TOTAL_COST_AFTER_OFFERS_ERROR)
		Double totalCostOffers,

		List<CartItemDTO> cartItems
) {
}
