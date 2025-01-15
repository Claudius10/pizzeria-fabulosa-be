package org.pizzeria.fabulosa.web.exceptions.constraints.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.pizzeria.fabulosa.web.exceptions.constraints.DoubleLengthValidator;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DoubleLengthValidator.class)
@Documented
public @interface DoubleLength {

	String message() default "Valor numérico no aceptado";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	double min();

	double max();
}