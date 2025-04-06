package org.pizzeria.fabulosa.web.dto.order.projection;

import org.pizzeria.fabulosa.utils.enums.OrderState;

public interface OrderSummaryProjection {

	Long getId();

	String getFormattedCreatedOn();

	OrderState getState();

	OrderDetailsView getOrderDetails();

	CartView getCart();

	interface CartView {
		Long getId();

		Integer getTotalQuantity();

		Double getTotalCost();

		Double getTotalCostOffers();
	}

	interface OrderDetailsView {
		String getPaymentMethod();
	}
}