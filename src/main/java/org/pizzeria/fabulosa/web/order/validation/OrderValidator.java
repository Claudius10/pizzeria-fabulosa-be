package org.pizzeria.fabulosa.web.order.validation;

import org.pizzeria.fabulosa.web.constants.ValidationResponses;

import java.time.LocalDateTime;

public final class OrderValidator {

	private final static int UPDATE_TIME_LIMIT_MIN = 10;

	public static OrderValidationResult validateDelete(LocalDateTime createdOn) {

		LocalDateTime limit = createdOn.plusMinutes(UPDATE_TIME_LIMIT_MIN);

		if (limit.isBefore(LocalDateTime.now())) {
			return OrderValidationResult.builder().invalid(ValidationResponses.ORDER_DELETE_TIME_ERROR).build();
		}

		return OrderValidationResult.builder().valid().build();
	}
}