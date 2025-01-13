package org.pizzeria.fabulosa.order;

import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pizzeria.fabulosa.entity.cart.Cart;
import org.pizzeria.fabulosa.entity.cart.CartItem;
import org.pizzeria.fabulosa.web.order.validation.OrderValidator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SpringBootTest} annotation contains @ExtendWith(SpringExtension.class).
 *
 * <p>
 * Annotation that can be specified on a test class that runs Spring Boot based tests. Provides the following features over and above the regular Spring TestContext Framework:
 * <p>
 * Uses SpringBootContextLoader as the default ContextLoader when no specific @ContextConfiguration(loader=...) is defined.
 * <p>
 * Automatically searches for a @SpringBootConfiguration when nested @Configuration is not used, and no explicit classes are specified.
 * <p>
 * Allows custom Environment properties to be defined using the properties attribute.
 * <p>
 * Allows application arguments to be defined using the args attribute.
 * <p>
 * Provides support for different webEnvironment modes, including the ability to start a fully running web server listening on a defined or random port.
 * <p>
 * Registers a TestRestTemplate and/or WebTestClient bean for use in web tests that are using a fully running web server.
 * <p>
 * <p>
 * {@link SpringExtension} {@code @ExtendWith(SpringExtension.class)}
 * <p>
 * SpringExtension integrates the Spring TestContext Framework into JUnit 5's Jupiter programming model.
 * <p>
 * <p>
 * {@link SpringJUnitConfig}
 * <p>
 * {@code @SpringJUnitConfig} is a composed annotation that combines @ExtendWith(SpringExtension.class) from JUnit Jupiter with @ContextConfiguration from the Spring TestContext Framework.
 * <p>
 * <p>
 * {@link ContextConfiguration}
 * <p>
 * {@code @ContextConfiguration} defines class-level metadata that is used to determine how to load and configure an ApplicationContext for integration tests.
 * <p>
 * <p>
 * {@link MockitoExtension} {@code @ExtendWith(MockitoExtension.class)}
 * <p>
 * Extension that initializes mocks and handles strict stubbing.
 */

@SpringJUnitConfig
class OrderValidatorTests {

	OrderValidator orderValidator = new OrderValidator(null);

	@Test
	void givenIsCartEmptyMethod_whenValidatingEmptyCart_thenReturnTrue() {
		Cart cart = new Cart.Builder().withEmptyItemList().build();
		boolean isCartEmpty = orderValidator.isCartEmpty(cart);
		assertTrue(isCartEmpty);
	}

	@Test
	void givenIsCartEmptyMethod_whenValidatingNonEmptyCart_thenReturnFalse() {
		Cart cart = new Cart.Builder().withCartItems
						(Collections.singletonList(CartItem.builder()
								.withPrice(5D)
								.withCode("B3L")
								.withQuantity(1)
								.build()))
				.withTotalQuantity(1)
				.withTotalCost(5D)
				.build();
		boolean isCartEmpty = orderValidator.isCartEmpty(cart);
		assertFalse(isCartEmpty);
	}

	@Test
	void givenIsCartEmptyMethod_whenValidatingNullCart_thenReturnTrue() {
		boolean isCartEmpty = orderValidator.isCartEmpty(null);
		assertTrue(isCartEmpty);
	}

	@Test
	void givenIsChangeRequestedValidMethod_whenRequestedChangeIsNotValid_thenReturnFalse() {
		// changeRequested > totalCostAfterOffers || changeRequested > totalCost
		double changeRequested = 10;
		double totalCost = 20;
		double totalCostAfterOffers = 13.3;
		boolean isChangeRequestedValid = orderValidator.isChangeRequestedValid(changeRequested, totalCostAfterOffers, totalCost);
		assertFalse(isChangeRequestedValid);
	}

	@Test
	void givenIsChangeRequestedValidMethod_whenRequestedChangeIsValid_thenReturnTrue() {
		// changeRequested > totalCostAfterOffers || changeRequested > totalCost
		double changeRequested = 20;
		double totalCost = 20;
		double totalCostAfterOffers = 13.3;
		boolean isChangeRequestedValid = orderValidator.isChangeRequestedValid(changeRequested, totalCostAfterOffers, totalCost);
		assertTrue(isChangeRequestedValid);
	}

	@Test
	void givenCalculatePaymentChangeMethod_whenRequestedChangeWithNoOffers_thenReturnCorrectValue() {
		double changeRequested = 20;
		double totalCost = 10;
		double totalCostAfterOffers = 0;
		double expectedOutput = 10;
		double actualOutput = orderValidator.calculatePaymentChange(changeRequested, totalCost, totalCostAfterOffers);
		assertEquals(expectedOutput, actualOutput);
	}

	@Test
	void givenCalculatePaymentChangeMethod_whenRequestedChangeWithOffers_thenReturnCorrectValue() {
		double changeRequested = 20;
		double totalCost = 20;
		double totalCostAfterOffers = 13.3;
		double expectedOutput = 6.7;
		double actualOutput = orderValidator.calculatePaymentChange(changeRequested, totalCost, totalCostAfterOffers);
		assertEquals(expectedOutput, actualOutput);
	}

	@Test
	void givenCalculatePaymentChangeMethod_whenNotRequestedChange_thenReturnNull() {
		double totalCost = 20;
		double totalCostAfterOffers = 13.3;
		Double output = orderValidator.calculatePaymentChange(null, totalCost, totalCostAfterOffers);
		assertNull(output);
	}
}