package org.pizzeria.fabulosa.web.validation.order;

public interface Validator<T> {

	ValidationResult validate(T object);
}
