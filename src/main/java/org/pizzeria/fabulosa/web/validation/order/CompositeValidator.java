package org.pizzeria.fabulosa.web.validation.order;

import java.util.Optional;

public interface CompositeValidator<T> {

	Optional<ValidationResult> validate(T object);
}
