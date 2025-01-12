package org.pizzeria.fabulosa.web.exceptions.constraints.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.pizzeria.fabulosa.web.exceptions.constraints.AddressValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {AddressValidator.class})
@Documented
public @interface ValidAddress {

	String message() default "El formato del domicilio no es válido";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
