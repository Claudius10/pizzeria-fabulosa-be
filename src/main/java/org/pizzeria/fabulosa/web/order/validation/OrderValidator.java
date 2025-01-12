package org.pizzeria.fabulosa.web.order.validation;

import org.pizzeria.fabulosa.entity.cart.Cart;
import org.pizzeria.fabulosa.entity.order.OrderDetails;

import java.time.LocalDateTime;

public interface OrderValidator {

	OrderValidationResult validate(Cart cart, OrderDetails orderDetails);

	OrderValidationResult validateUpdate(Cart cart, OrderDetails orderDetails, LocalDateTime createdOn);

	OrderValidationResult validateDelete(Long orderId);

	boolean isCartEmpty(Cart cart);

	boolean isChangeRequestedValid(Double changeRequested, Double totalCostAfterOffers, Double totalCost);

	boolean isOrderDataUpdateTimeLimitValid(LocalDateTime createdOn);

	Double calculatePaymentChange(Double changeRequested, Double totalCost, Double totalCostAfterOffers);
}
