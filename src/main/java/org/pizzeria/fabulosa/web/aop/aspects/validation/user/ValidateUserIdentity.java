package org.pizzeria.fabulosa.web.aop.aspects.validation.user;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.pizzeria.fabulosa.entity.error.Error;
import org.pizzeria.fabulosa.web.constants.SecurityResponses;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.api.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Aspect
public class ValidateUserIdentity {

	// The user id sent as a path variable or request param for endpoints that require it
	// must match the user id stored in the access token.

	@Around(value = "org.pizzeria.fabulosa.web.aop.pointcuts.UserPointCuts.requieresUserIdValidation() && args(.., userId, request)", argNames = "pjp,userId,request")
	public Object validate(ProceedingJoinPoint pjp, Long userId, HttpServletRequest request) throws Throwable {

		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.UNAUTHORIZED.name())
						.code(HttpStatus.UNAUTHORIZED.value())
						.isError(true)
						.build())
				.error(Error.builder()
						.id(UUID.randomUUID().getMostSignificantBits())
						.cause(SecurityResponses.USER_ID_NO_MATCH)
						.origin(ValidateUserIdentity.class.getSimpleName() + ".validate")
						.path(request.getPathInfo())
						.logged(false)
						.fatal(true)
						.build())
				.build();

		if (userId == null) {
			return ResponseEntity.status(HttpStatus.OK).body(response); // return OK to get the ResponseDTO in onSuccess callback
		}

		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		Jwt validatedAccessToken = (Jwt) authentication.getPrincipal();

		if (!String.valueOf(userId).equals(validatedAccessToken.getClaimAsString("userId"))) {
			return ResponseEntity.status(HttpStatus.OK).body(response); // return OK to get the ResponseDTO in onSuccess callback
		}

		return pjp.proceed();
	}
}