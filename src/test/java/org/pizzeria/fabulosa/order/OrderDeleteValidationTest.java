package org.pizzeria.fabulosa.order;

import org.junit.jupiter.api.Test;
import org.pizzeria.fabulosa.web.constants.ValidationResponses;
import org.pizzeria.fabulosa.web.order.validation.OrderValidationResult;
import org.pizzeria.fabulosa.web.order.validation.OrderValidator;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderDeleteValidationTest {

	private final OrderValidator orderValidator = new OrderValidator();

	@Test
	void givenOrderDeleteRequest_whenDeleteWindowPassed_thenReturnInvalidResult() {
		// Arrange

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime date = now.minusMinutes(15);

		// Act

		OrderValidationResult isDeleteRequestValid = orderValidator.validateDelete(date);

		// Assert
		assertAll(() -> {
			assertFalse(isDeleteRequestValid.isValid());
			assertEquals(ValidationResponses.ORDER_DELETE_TIME_ERROR, isDeleteRequestValid.getMessage());
		});
	}

	@Test
	void givenOrderDeleteRequest_whenDeleteWindowDidNotPass_thenReturnValidResult() {
		// Arrange

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime date = now.plusMinutes(15);

		// Act

		OrderValidationResult isDeleteRequestValid = orderValidator.validateDelete(date);

		// Assert

		assertAll(() -> {
			assertTrue(isDeleteRequestValid.isValid());
			assertNull(isDeleteRequestValid.getMessage());
		});
	}
}