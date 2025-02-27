package org.pizzeria.fabulosa.web.order.validation;

import org.pizzeria.fabulosa.entity.cart.Cart;
import org.pizzeria.fabulosa.entity.order.OrderDetails;
import org.pizzeria.fabulosa.web.constants.ValidationResponses;

import java.time.LocalDateTime;

public final class OrderValidator {

	private final static int UPDATE_TIME_LIMIT_MIN = 10;

	public static OrderValidationResult validate(Cart cart, OrderDetails orderDetails) {

		if (isCartEmpty(cart)) {
			return OrderValidationResult.builder().invalid(ValidationResponses.CART_IS_EMPTY).build();
		}

		if (!isChangeRequestedValid(orderDetails.getBillToChange(), cart.getTotalCostOffers(), cart.getTotalCost())) {
			return OrderValidationResult.builder().invalid(ValidationResponses.ORDER_DETAILS_BILL).build();
		}

		orderDetails.setChangeToGive(calculatePaymentChange(orderDetails.getBillToChange(), cart.getTotalCost(), cart.getTotalCostOffers()));
		return OrderValidationResult.builder().valid().build();
	}

	public static OrderValidationResult validateDelete(LocalDateTime createdOn) {

		LocalDateTime limit = createdOn.plusMinutes(UPDATE_TIME_LIMIT_MIN);

		if (limit.isBefore(LocalDateTime.now())) {
			return OrderValidationResult.builder().invalid(ValidationResponses.ORDER_DELETE_TIME_ERROR).build();
		}

		return OrderValidationResult.builder().valid().build();
	}

	public static boolean isCartEmpty(Cart cart) {
		return cart == null || cart.getCartItems().isEmpty() || cart.getTotalQuantity() == 0;
	}

	// changeRequested > totalCostAfterOffers || changeRequested > totalCost
	public static boolean isChangeRequestedValid(Double billToChange, Double totalCostAfterOffers, Double totalCost) {
		if (billToChange == null) {
			return true;
		}
		return (totalCostAfterOffers <= 0 || billToChange >= totalCostAfterOffers) && (totalCostAfterOffers != 0 || billToChange >= totalCost);
	}

	// changeRequested == null || (changeRequested - totalCostOffers || totalCost)
	public static Double calculatePaymentChange(Double changeRequested, Double totalCost, Double totalCostAfterOffers) {
		if (changeRequested == null) {
			return null;
		}

		if (totalCostAfterOffers > 0) {
			return (double) Math.round((changeRequested - totalCostAfterOffers) * 100) / 100;
		}

		return (double) Math.round((changeRequested - totalCost) * 100) / 100;
	}
}