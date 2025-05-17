package org.pizzeria.fabulosa.web.validation.order;

public record ValidationResult(
		String message,
		Boolean valid
) {
}