package org.pizzeria.fabulosa.web.order.validation;

import org.pizzeria.fabulosa.entity.cart.Cart;
import org.pizzeria.fabulosa.entity.order.OrderDetails;
import org.pizzeria.fabulosa.services.order.OrderService;
import org.pizzeria.fabulosa.web.constants.ValidationResponses;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderValidatorImpl implements OrderValidator {

	private final static int UPDATE_TIME_LIMIT_MIN = 10;

	private final OrderService orderService;

	public OrderValidatorImpl(OrderService orderService) {
		this.orderService = orderService;
	}

	@Override
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

	@Override
	public OrderValidationResult validateUpdate(Cart cart, OrderDetails orderDetails, LocalDateTime createdOn) {
		if (!isOrderDataUpdateTimeLimitValid(createdOn)) {
			return new OrderValidationResult(ValidationResponses.ORDER_UPDATE_TIME_ERROR);
		}

		return validate(cart, orderDetails);
	}

	public OrderValidationResult validateDelete(Long orderId) {
		LocalDateTime createdOn = orderService.findCreatedOnById(orderId);

		if (LocalDateTime.now().isAfter(createdOn.plusMinutes(UPDATE_TIME_LIMIT_MIN))) {
			return new OrderValidationResult(ValidationResponses.ORDER_DELETE_TIME_ERROR);
		}

		return new OrderValidationResult();
	}

	@Override
	public boolean isCartEmpty(Cart cart) {
		return cart == null || cart.getCartItems().isEmpty() || cart.getTotalQuantity() == 0;
	}

	// changeRequested > totalCostAfterOffers || changeRequested > totalCost
	@Override
	public boolean isChangeRequestedValid(Double billToChange, Double totalCostAfterOffers, Double totalCost) {
		if (billToChange == null) {
			return true;
		}
		return (totalCostAfterOffers <= 0 || billToChange >= totalCostAfterOffers) && (totalCostAfterOffers != 0 || billToChange >= totalCost);
	}

	@Override
	public boolean isOrderDataUpdateTimeLimitValid(LocalDateTime createdOn) {
		return LocalDateTime.now().isBefore(createdOn.plusMinutes(UPDATE_TIME_LIMIT_MIN));
	}

	// changeRequested == null || (changeRequested - totalCostOffers || totalCost)
	@Override
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