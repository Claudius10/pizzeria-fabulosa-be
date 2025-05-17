package org.pizzeria.fabulosa.web.config.validator;

import org.pizzeria.fabulosa.web.validation.order.CompositeValidator;
import org.pizzeria.fabulosa.web.validation.order.OrderValidatorInput;
import org.pizzeria.fabulosa.web.validation.order.Validator;
import org.pizzeria.fabulosa.web.validation.order.impl.CartValidator;
import org.pizzeria.fabulosa.web.validation.order.impl.DeleteTimeLimitValidator;
import org.pizzeria.fabulosa.web.validation.order.impl.NewOrderValidator;
import org.pizzeria.fabulosa.web.validation.order.impl.OrderDetailsValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class ValidatorConfig {

	@Bean
	CompositeValidator<OrderValidatorInput> newOrderValidator() {
		NewOrderValidator newOrderValidator = new NewOrderValidator();
		newOrderValidator.setValidators(List.of(new CartValidator(), new OrderDetailsValidator()));
		return newOrderValidator;
	}

	@Bean
	Validator<LocalDateTime> deleteOrderValidator() {
		return new DeleteTimeLimitValidator();
	}
}
