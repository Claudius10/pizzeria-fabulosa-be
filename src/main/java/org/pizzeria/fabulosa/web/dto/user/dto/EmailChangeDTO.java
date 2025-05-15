package org.pizzeria.fabulosa.web.dto.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.pizzeria.fabulosa.web.util.constant.ValidationResponses;

public record EmailChangeDTO(
		@Email(message = ValidationResponses.EMAIL_INVALID)
		@NotBlank(message = ValidationResponses.EMAIL_INVALID)
		String email,

		@NotBlank(message = ValidationResponses.PASSWORD_INVALID)
		String password) {
}