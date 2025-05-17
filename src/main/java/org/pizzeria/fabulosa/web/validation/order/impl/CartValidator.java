package org.pizzeria.fabulosa.web.validation.order.impl;

import org.pizzeria.fabulosa.web.dto.order.CartDTO;
import org.pizzeria.fabulosa.web.util.constant.ValidationResponses;
import org.pizzeria.fabulosa.web.validation.order.OrderValidatorInput;
import org.pizzeria.fabulosa.web.validation.order.ValidationResult;
import org.pizzeria.fabulosa.web.validation.order.Validator;

public class CartValidator implements Validator<OrderValidatorInput> {

	public ValidationResult validate(OrderValidatorInput order) {

		if (isCartEmpty(order.cart())) {
			return new ValidationResult(ValidationResponses.CART_IS_EMPTY, false);
		}

		return new ValidationResult(null, true);
	}

	private boolean isCartEmpty(CartDTO cart) {
		return cart == null || cart.cartItems().isEmpty() || cart.totalQuantity() == 0;
	}
}
