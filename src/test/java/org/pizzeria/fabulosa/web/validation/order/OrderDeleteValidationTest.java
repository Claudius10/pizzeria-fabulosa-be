package org.pizzeria.fabulosa.web.validation.order;

import org.junit.jupiter.api.Test;
import org.pizzeria.fabulosa.web.util.constant.ValidationResponses;
import org.pizzeria.fabulosa.web.validation.order.impl.DeleteTimeLimitValidator;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OrderDeleteValidationTest {

	@Test
	void givenOrderDeleteRequest_whenDeleteWindowPassed_thenReturnInvalidResult() {
		// Arrange

		DeleteTimeLimitValidator deleteTimeLimitValidator = new DeleteTimeLimitValidator();
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime date = now.minusMinutes(15);

		// Act

		ValidationResult isDeleteRequestValid = deleteTimeLimitValidator.validate(date);

		// Assert

		assertThat(isDeleteRequestValid.valid()).isFalse();
		assertThat(isDeleteRequestValid.message()).isEqualTo(ValidationResponses.ORDER_DELETE_TIME_ERROR);
	}

	@Test
	void givenOrderDeleteRequest_whenDeleteWindowDidNotPass_thenReturnValidResult() {
		// Arrange

		DeleteTimeLimitValidator deleteTimeLimitValidator = new DeleteTimeLimitValidator();
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime date = now.plusMinutes(15);

		// Act

		ValidationResult isDeleteRequestValid = deleteTimeLimitValidator.validate(date);

		// Assert

		assertThat(isDeleteRequestValid.valid()).isTrue();
		assertThat(isDeleteRequestValid.message()).isNull();
	}
}