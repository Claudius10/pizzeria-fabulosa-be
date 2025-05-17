package org.pizzeria.fabulosa.web.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.pizzeria.fabulosa.web.util.constant.ValidationResponses;
import org.pizzeria.fabulosa.web.util.constant.ValidationRules;
import org.pizzeria.fabulosa.web.validation.constraints.annotation.DoubleLengthNullable;

public record OrderDetailsDTO(

		@NotBlank(message = ValidationResponses.ORDER_DETAILS_DELIVERY_HOUR)
		String deliveryTime,

		@NotBlank(message = ValidationResponses.ORDER_DETAILS_PAYMENT)
		String paymentMethod,

		@DoubleLengthNullable(min = 0, max = 5, message = ValidationResponses.ORDER_DETAILS_BILL)
		Double billToChange,

		@Pattern(regexp = ValidationRules.COMPLEX_LETTERS_NUMBERS_MAX_150_OPTIONAL, message = ValidationResponses.ORDER_DETAILS_COMMENT)
		String comment,

		@NotNull
		Boolean storePickUp,

		Double changeToGive
) {
}
