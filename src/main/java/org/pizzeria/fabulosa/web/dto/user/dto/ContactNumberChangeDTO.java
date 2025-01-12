package org.pizzeria.fabulosa.web.dto.user.dto;

import jakarta.validation.constraints.NotBlank;
import org.pizzeria.fabulosa.web.constants.ValidationResponses;
import org.pizzeria.fabulosa.web.exceptions.constraints.annotation.IntegerLength;

public record ContactNumberChangeDTO(
		@IntegerLength(min = 9, max = 9, message = ValidationResponses.NUMBER_INVALID)
		Integer contactNumber,

		@NotBlank(message = ValidationResponses.PASSWORD_INVALID)
		String password) {
}