package org.pizzeria.fabulosa.web.dto.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.pizzeria.fabulosa.web.error.constraints.annotation.FieldMatch;
import org.pizzeria.fabulosa.web.error.constraints.annotation.IntegerLength;
import org.pizzeria.fabulosa.web.util.constant.ValidationResponses;
import org.pizzeria.fabulosa.web.util.constant.ValidationRules;

@FieldMatch.List({
		@FieldMatch(first = "email", second = "matchingEmail", message = ValidationResponses.EMAIL_NO_MATCH),
		@FieldMatch(first = "password", second = "matchingPassword", message = ValidationResponses.PASSWORD_NO_MATCH)})
public record RegisterDTO(
		@Pattern(regexp = ValidationRules.SIMPLE_LETTERS_ONLY_MAX_50_INSENSITIVE_REQUIERED, message = ValidationResponses.NAME_INVALID)
		String name,

		@Email(message = ValidationResponses.EMAIL_INVALID)
		@NotBlank(message = ValidationResponses.EMAIL_INVALID)
		String email,

		@Email(message = ValidationResponses.EMAIL_INVALID)
		@NotBlank(message = ValidationResponses.EMAIL_INVALID)
		String matchingEmail,

		@IntegerLength(min = 9, max = 9, message = ValidationResponses.NUMBER_INVALID)
		Integer contactNumber,

		@Pattern(regexp = ValidationRules.USER_PASSWORD, message = ValidationResponses.PASSWORD_INVALID)
		String password,

		@Pattern(regexp = ValidationRules.USER_PASSWORD, message = ValidationResponses.PASSWORD_INVALID)
		String matchingPassword) {
}