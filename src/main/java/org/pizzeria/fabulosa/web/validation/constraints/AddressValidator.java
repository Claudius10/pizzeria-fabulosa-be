package org.pizzeria.fabulosa.web.validation.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.pizzeria.fabulosa.web.dto.order.AddressDTO;
import org.pizzeria.fabulosa.web.util.constant.ValidationResponses;
import org.pizzeria.fabulosa.web.util.constant.ValidationRules;
import org.pizzeria.fabulosa.web.validation.constraints.annotation.ValidAddress;

public class AddressValidator implements ConstraintValidator<ValidAddress, AddressDTO> {

	@Override
	public void initialize(ValidAddress constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(AddressDTO address, ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();

		// NOTE - if store pick up is selected, only id is present on AddressDTO

		if (null != address.street() && !address.street().matches(ValidationRules.SIMPLE_LETTERS_ONLY_MAX_50_INSENSITIVE_REQUIERED)) {
			context.buildConstraintViolationWithTemplate(ValidationResponses.ADDRESS_STREET).addPropertyNode("street").addConstraintViolation();
			return false;
		}

		if (null != address.number()) {
			int streetNrDigits = String.valueOf(address.number()).length();
			if (streetNrDigits > 4) {
				context.buildConstraintViolationWithTemplate(ValidationResponses.ADDRESS_STREET_NUMBER).addPropertyNode("number").addConstraintViolation();
				return false;
			}
		}

		if (address.details() != null && !address.details().matches(ValidationRules.COMPLEX_LETTERS_NUMBERS_MAX_150_OPTIONAL)) {
			context.buildConstraintViolationWithTemplate(ValidationResponses.ADDRESS_DETAILS).addPropertyNode("details").addConstraintViolation();
			return false;
		}

		return true;
	}
}