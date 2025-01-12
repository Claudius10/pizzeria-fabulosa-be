package org.pizzeria.fabulosa.web.exceptions.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.pizzeria.fabulosa.web.exceptions.constraints.annotation.IntegerLength;

public class IntegerLengthValidator implements ConstraintValidator<IntegerLength, Integer> {

	private int min;

	private int max;

	@Override
	public void initialize(IntegerLength integerLength) {
		this.min = integerLength.min();
		this.max = integerLength.max();
	}

	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		// validate whatever value is null 
		// and
		// validate whatever the int field value is between min and max
		if (value == null) {
			return false;
		}
		int valueLength = String.valueOf(value.intValue()).length();
		return (valueLength >= min && valueLength <= max);
	}
}