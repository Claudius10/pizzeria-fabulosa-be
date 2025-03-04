package org.pizzeria.fabulosa.web.dto.order.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.pizzeria.fabulosa.web.constants.ValidationResponses;
import org.pizzeria.fabulosa.web.constants.ValidationRules;
import org.pizzeria.fabulosa.web.error.constraints.annotation.IntegerLength;

public record CustomerDTO(
		@Pattern(regexp = ValidationRules.SIMPLE_LETTERS_ONLY_MAX_50_INSENSITIVE_REQUIERED, message = ValidationResponses.NAME_INVALID)
		String name,

		@IntegerLength(min = 9, max = 9, message = ValidationResponses.NUMBER_INVALID)
		Integer contactNumber,

		@Email(message = ValidationResponses.EMAIL_INVALID)
		String email
) {
}
