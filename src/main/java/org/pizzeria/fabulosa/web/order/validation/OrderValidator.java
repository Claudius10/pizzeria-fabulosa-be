package org.pizzeria.fabulosa.web.order.validation;

import org.pizzeria.fabulosa.entity.cart.Cart;
import org.pizzeria.fabulosa.entity.order.OrderDetails;
import org.pizzeria.fabulosa.services.order.OrderService;
import org.pizzeria.fabulosa.web.constants.ValidationResponses;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderValidator {

	// TODO - refactor this to no need OrderService

	private final static int UPDATE_TIME_LIMIT_MIN = 10;

	private final OrderService orderService;

	public OrderValidator(OrderService orderService) {
		this.orderService = orderService;
	}

	public OrderValidationResult validate(Cart cart, OrderDetails orderDetails) {
		if (isCartEmpty(cart)) {
			return new OrderValidationResult(ValidationResponses.CART_IS_EMPTY);
		}

		if (!isChangeRequestedValid(orderDetails.getBillToChange(), cart.getTotalCostOffers(), cart.getTotalCost())) {
			return new OrderValidationResult(ValidationResponses.ORDER_DETAILS_BILL);
		}

		orderDetails.setChangeToGive(calculatePaymentChange(orderDetails.getBillToChange(), cart.getTotalCost(), cart.getTotalCostOffers()));
		return new OrderValidationResult();
	}

	public OrderValidationResult validateDelete(Long orderId) {
		LocalDateTime createdOn = orderService.findCreatedOnById(orderId);

		if (LocalDateTime.now().isAfter(createdOn.plusMinutes(UPDATE_TIME_LIMIT_MIN))) {
			return new OrderValidationResult(ValidationResponses.ORDER_DELETE_TIME_ERROR);
		}

		return new OrderValidationResult();
	}

	public boolean isCartEmpty(Cart cart) {
		return cart == null || cart.getCartItems().isEmpty() || cart.getTotalQuantity() == 0;
	}

	// changeRequested > totalCostAfterOffers || changeRequested > totalCost
	public boolean isChangeRequestedValid(Double billToChange, Double totalCostAfterOffers, Double totalCost) {
		if (billToChange == null) {
			return true;
		}
		return (totalCostAfterOffers <= 0 || billToChange >= totalCostAfterOffers) && (totalCostAfterOffers != 0 || billToChange >= totalCost);
	}

	// changeRequested == null || (changeRequested - totalCostOffers || totalCost)
	public Double calculatePaymentChange(Double changeRequested, Double totalCost, Double totalCostAfterOffers) {
		if (changeRequested == null) {
			return null;
		}

		if (totalCostAfterOffers > 0) {
			return (double) Math.round((changeRequested - totalCostAfterOffers) * 100) / 100;
		}

		return (double) Math.round((changeRequested - totalCost) * 100) / 100;
	}
}