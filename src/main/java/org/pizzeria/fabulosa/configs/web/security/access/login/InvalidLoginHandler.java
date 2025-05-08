package org.pizzeria.fabulosa.configs.web.security.access.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class InvalidLoginHandler implements AuthenticationFailureHandler {

	private final HandlerExceptionResolver resolver;

	public InvalidLoginHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
		this.resolver = resolver;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
		// return an empty ModelAndView so ex can be picked up by @ControllerAdvice
		resolver.resolveException(request, response, null, exception);
	}
}