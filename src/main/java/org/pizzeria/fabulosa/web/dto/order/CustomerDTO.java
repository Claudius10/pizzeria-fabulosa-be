package org.pizzeria.fabulosa.web.dto.order;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.pizzeria.fabulosa.web.util.constant.ValidationResponses;
import org.pizzeria.fabulosa.web.util.constant.ValidationRules;
import org.pizzeria.fabulosa.web.validation.constraints.annotation.IntegerLength;

public record CustomerDTO(

		@NotNull
		@Pattern(regexp = ValidationRules.SIMPLE_LETTERS_ONLY_MAX_50_INSENSITIVE_REQUIERED, message = ValidationResponses.NAME_INVALID)
		String name,

		@NotNull
		@IntegerLength(min = 9, max = 9, message = ValidationResponses.NUMBER_INVALID)
		Integer contactNumber,

		@NotNull
		@Email(message = ValidationResponses.EMAIL_INVALID)
		String email
) {
}
