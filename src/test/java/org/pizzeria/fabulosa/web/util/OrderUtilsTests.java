package org.pizzeria.fabulosa.web.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderUtilsTests {

	@Test
	void givenCalculatePaymentChangeMethod_whenRequestedChangeWithNoOffers_thenReturnCorrectValue() {
		double changeRequested = 20;
		double totalCost = 10;
		double totalCostAfterOffers = 0;
		double expectedOutput = 10;
		double actualOutput = OrderUtils.calculatePaymentChange(changeRequested, totalCost, totalCostAfterOffers);
		assertThat(expectedOutput).isEqualTo(actualOutput);
	}

	@Test
	void givenCalculatePaymentChangeMethod_whenRequestedChangeWithOffers_thenReturnCorrectValue() {
		double changeRequested = 20;
		double totalCost = 20;
		double totalCostAfterOffers = 13.3;
		double expectedOutput = 6.7;
		double actualOutput = OrderUtils.calculatePaymentChange(changeRequested, totalCost, totalCostAfterOffers);
		assertThat(expectedOutput).isEqualTo(actualOutput);
	}

	@Test
	void givenCalculatePaymentChangeMethod_whenNotRequestedChange_thenReturnNull() {
		double totalCost = 20;
		double totalCostAfterOffers = 13.3;
		Double output = OrderUtils.calculatePaymentChange(null, totalCost, totalCostAfterOffers);
		assertThat(output).isNull();
	}
}
