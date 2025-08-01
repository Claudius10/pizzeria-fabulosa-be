package org.pizzeria.fabulosa.common.entity.projection;

public interface OrderSummaryProjection {

	Long getId();

	String getFormattedCreatedOn();

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