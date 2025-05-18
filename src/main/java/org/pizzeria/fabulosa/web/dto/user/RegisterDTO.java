package org.pizzeria.fabulosa.web.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.pizzeria.fabulosa.web.util.constant.ValidationResponses;
import org.pizzeria.fabulosa.web.util.constant.ValidationRules;
import org.pizzeria.fabulosa.web.validation.constraints.annotation.FieldMatch;
import org.pizzeria.fabulosa.web.validation.constraints.annotation.IntegerLength;

@FieldMatch.List({
		@FieldMatch(first = "email", second = "matchingEmail", message = ValidationResponses.EMAIL_NO_MATCH),
		@FieldMatch(first = "password", second = "matchingPassword", message = ValidationResponses.PASSWORD_NO_MATCH)})
public record RegisterDTO(
		@NotNull
		@Pattern(regexp = ValidationRules.SIMPLE_LETTERS_ONLY_MAX_50_INSENSITIVE_REQUIERED, message = ValidationResponses.NAME_INVALID)
		String name,

		@Email(message = ValidationResponses.EMAIL_INVALID)
		@NotBlank(message = ValidationResponses.EMAIL_INVALID)
		String email,

		@Email(message = ValidationResponses.EMAIL_INVALID)
		@NotBlank(message = ValidationResponses.EMAIL_INVALID)
		String matchingEmail,

		@NotNull
		@IntegerLength(min = 9, max = 9, message = ValidationResponses.NUMBER_INVALID)
		Integer contactNumber,

		@NotNull
		@Pattern(regexp = ValidationRules.USER_PASSWORD, message = ValidationResponses.PASSWORD_INVALID)
		String password,

		@NotNull
		@Pattern(regexp = ValidationRules.USER_PASSWORD, message = ValidationResponses.PASSWORD_INVALID)
		String matchingPassword) {
}