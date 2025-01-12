package org.pizzeria.fabulosa.web.dto.user.dto;

import jakarta.validation.constraints.NotBlank;
import org.pizzeria.fabulosa.web.constants.ValidationResponses;

public record PasswordDTO(
		@NotBlank(message = ValidationResponses.PASSWORD_INVALID)
		String password) {
}