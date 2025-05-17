package org.pizzeria.fabulosa.web.validation.order.impl;

import org.pizzeria.fabulosa.web.dto.order.CartDTO;
import org.pizzeria.fabulosa.web.dto.order.OrderDetailsDTO;
import org.pizzeria.fabulosa.web.util.constant.ValidationResponses;
import org.pizzeria.fabulosa.web.validation.order.OrderValidatorInput;
import org.pizzeria.fabulosa.web.validation.order.ValidationResult;
import org.pizzeria.fabulosa.web.validation.order.Validator;

public class OrderDetailsValidator implements Validator<OrderValidatorInput> {

	public ValidationResult validate(OrderValidatorInput order) {

		CartDTO cart = order.cart();
		OrderDetailsDTO orderDetails = order.orderDetails();
		Double billToChange = orderDetails.billToChange() == null ? 0 : orderDetails.billToChange();
		Double totalCost = cart.totalCost();
		Double totalCostOffers = cart.totalCostOffers() == null ? 0 : cart.totalCostOffers();

		if (!isChangeRequestedValid(billToChange, totalCostOffers, totalCost)) {
			return new ValidationResult(ValidationResponses.ORDER_DETAILS_BILL, false);
		}

		return new ValidationResult(null, true);
	}

	// changeRequested > totalCostAfterOffers || changeRequested > totalCost
	private boolean isChangeRequestedValid(Double billToChange, Double totalCostAfterOffers, Double totalCost) {
		if (billToChange == null || billToChange == 0) {
			return true;
		}
		return (totalCostAfterOffers <= 0 || billToChange >= totalCostAfterOffers) && (totalCostAfterOffers != 0 || billToChange >= totalCost);
	}
}
