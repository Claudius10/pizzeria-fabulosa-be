package org.pizzeria.fabulosa.utils.enums;

import lombok.ToString;

@ToString
public enum OrderState {
	PREPARING("PREPARING"),
	READY("READY"),
	DELIVERED("DELIVERED"),
	CANCELLED("CANCELLED");

	private final String state;

	OrderState(String state) {
		this.state = state;
	}
}
