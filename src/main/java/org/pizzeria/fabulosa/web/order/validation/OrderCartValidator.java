package org.pizzeria.fabulosa.web.order.validation;

import org.pizzeria.fabulosa.common.entity.cart.Cart;
import org.pizzeria.fabulosa.web.util.constant.ValidationResponses;

public class OrderCartValidator {

	public static OrderValidationResult validate(Cart cart) {

		if (isCartEmpty(cart)) {
			return OrderValidationResult.builder().invalid(ValidationResponses.CART_IS_EMPTY).build();
		}

		return OrderValidationResult.builder().valid().build();
	}

	public static boolean isCartEmpty(Cart cart) {
		return cart == null || cart.getCartItems().isEmpty() || cart.getTotalQuantity() == 0;
	}
}
