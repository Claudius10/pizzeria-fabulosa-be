package org.pizzeria.fabulosa.web.error.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.pizzeria.fabulosa.web.error.constraints.annotation.DoubleLengthNullable;

public class DoubleLengthNullableValidator implements ConstraintValidator<DoubleLengthNullable, Double> {

	private double min;

	private double max;

	@Override
	public void initialize(DoubleLengthNullable doubleLength) {
		this.min = doubleLength.min();
		this.max = doubleLength.max();
	}

	@Override
	public boolean isValid(Double value, ConstraintValidatorContext context) {
		// validate whatever the double field value is between min and max
		if (value != null) {
			int length = String.valueOf(value.doubleValue()).length();
			return (length >= min && length <= max);
		} else {
			// return true if value is null
			return true;
		}
	}
}