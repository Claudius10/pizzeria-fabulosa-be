package org.pizzeria.fabulosa.web.dto.user.dto;

import jakarta.validation.constraints.NotBlank;
import org.pizzeria.fabulosa.web.error.constraints.annotation.IntegerLength;
import org.pizzeria.fabulosa.web.util.constant.ValidationResponses;

public record ContactNumberChangeDTO(
		@IntegerLength(min = 9, max = 9, message = ValidationResponses.NUMBER_INVALID)
		Integer contactNumber,

		@NotBlank(message = ValidationResponses.PASSWORD_INVALID)
		String password) {
}