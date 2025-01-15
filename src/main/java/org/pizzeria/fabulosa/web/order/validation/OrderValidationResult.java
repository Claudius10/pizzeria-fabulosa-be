package org.pizzeria.fabulosa.web.order.validation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderValidationResult {

	private boolean isValid;

	private String message;

	private OrderValidationResult(boolean valid, String message) {
		this.isValid = valid;
		this.message = message;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private boolean isValid;
		private String message;

		private Builder() {
		}

		public Builder valid() {
			isValid = true;
			message = null;
			return this;
		}

		public Builder invalid(String message) {
			isValid = false;
			this.message = message;
			return this;
		}

		public OrderValidationResult build() {
			return new OrderValidationResult(isValid, message);
		}
	}
}