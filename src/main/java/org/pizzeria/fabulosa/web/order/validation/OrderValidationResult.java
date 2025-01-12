package org.pizzeria.fabulosa.web.order.validation;

public class OrderValidationResult {

	private final boolean isValid;

	private final boolean isCartUpdateValid;

	private final String message;

	public OrderValidationResult() {
		this.isValid = true;
		this.isCartUpdateValid = true;
		this.message = null;
	}

	public OrderValidationResult(String message) {
		this.isValid = false;
		this.isCartUpdateValid = false;
		this.message = message;
	}

	public boolean isValid() {
		return isValid;
	}

	public boolean isCartUpdateValid() {
		return isCartUpdateValid;
	}

	public String getMessage() {
		return message;
	}
}