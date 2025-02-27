package org.pizzeria.fabulosa.web.aop.aspects.validation;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Aspect
public class ValidateUserAccountUpdate {

	private final AuthenticationManager authenticationManager;

	@Before(value = "(org.pizzeria.fabulosa.web.aop.pointcuts.UserPointCuts.userAccountUpdate() || org.pizzeria.fabulosa.web.aop.pointcuts.UserPointCuts" +
			".userAccountDelete()) && args(password, ..)", argNames = "password")
	public void verifyPassword(String password) {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		Jwt jwt = (Jwt) authentication.getPrincipal();
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwt.getSubject(), password));
	}
}