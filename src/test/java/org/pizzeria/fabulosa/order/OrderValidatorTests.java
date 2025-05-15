package org.pizzeria.fabulosa.order;

import org.junit.jupiter.api.Test;
import org.pizzeria.fabulosa.common.entity.cart.Cart;
import org.pizzeria.fabulosa.common.entity.cart.CartItem;
import org.pizzeria.fabulosa.web.order.validation.OrderCartValidator;
import org.pizzeria.fabulosa.web.order.validation.OrderDetailsValidator;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class OrderValidatorTests {

	@Test
	void givenIsCartEmptyMethod_whenValidatingEmptyCart_thenReturnTrue() {
		Cart cart = new Cart.Builder().withEmptyItemList().build();
		boolean isCartEmpty = OrderCartValidator.isCartEmpty(cart);
		assertTrue(isCartEmpty);
	}

	@Test
	void givenIsCartEmptyMethod_whenValidatingNonEmptyCart_thenReturnFalse() {
		Cart cart = new Cart.Builder().withCartItems
						(Collections.singletonList(CartItem.builder()
								.withPrice(5D)
								.withQuantity(1)
								.build()))
				.withTotalQuantity(1)
				.withTotalCost(5D)
				.build();
		boolean isCartEmpty = OrderCartValidator.isCartEmpty(cart);
		assertFalse(isCartEmpty);
	}

	@Test
	void givenIsCartEmptyMethod_whenValidatingNullCart_thenReturnTrue() {
		boolean isCartEmpty = OrderCartValidator.isCartEmpty(null);
		assertTrue(isCartEmpty);
	}

	@Test
	void givenIsChangeRequestedValidMethod_whenRequestedChangeIsNotValid_thenReturnFalse() {
		// changeRequested > totalCostAfterOffers || changeRequested > totalCost
		double changeRequested = 10;
		double totalCost = 20;
		double totalCostAfterOffers = 13.3;
		boolean isChangeRequestedValid = OrderDetailsValidator.isChangeRequestedValid(changeRequested, totalCostAfterOffers, totalCost);
		assertFalse(isChangeRequestedValid);
	}

	@Test
	void givenIsChangeRequestedValidMethod_whenRequestedChangeIsValid_thenReturnTrue() {
		// changeRequested > totalCostAfterOffers || changeRequested > totalCost
		double changeRequested = 20;
		double totalCost = 20;
		double totalCostAfterOffers = 13.3;
		boolean isChangeRequestedValid = OrderDetailsValidator.isChangeRequestedValid(changeRequested, totalCostAfterOffers, totalCost);
		assertTrue(isChangeRequestedValid);
	}

	@Test
	void givenCalculatePaymentChangeMethod_whenRequestedChangeWithNoOffers_thenReturnCorrectValue() {
		double changeRequested = 20;
		double totalCost = 10;
		double totalCostAfterOffers = 0;
		double expectedOutput = 10;
		double actualOutput = OrderDetailsValidator.calculatePaymentChange(changeRequested, totalCost, totalCostAfterOffers);
		assertEquals(expectedOutput, actualOutput);
	}

	@Test
	void givenCalculatePaymentChangeMethod_whenRequestedChangeWithOffers_thenReturnCorrectValue() {
		double changeRequested = 20;
		double totalCost = 20;
		double totalCostAfterOffers = 13.3;
		double expectedOutput = 6.7;
		double actualOutput = OrderDetailsValidator.calculatePaymentChange(changeRequested, totalCost, totalCostAfterOffers);
		assertEquals(expectedOutput, actualOutput);
	}

	@Test
	void givenCalculatePaymentChangeMethod_whenNotRequestedChange_thenReturnNull() {
		double totalCost = 20;
		double totalCostAfterOffers = 13.3;
		Double output = OrderDetailsValidator.calculatePaymentChange(null, totalCost, totalCostAfterOffers);
		assertNull(output);
	}
}