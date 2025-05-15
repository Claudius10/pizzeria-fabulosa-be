package org.pizzeria.fabulosa.security.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.pizzeria.fabulosa.common.entity.error.Error;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.util.constant.SecurityResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public final class UserSecurity {

	public static boolean valid(Long id) {
		if (id == null) {
			return false;
		}

		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		Jwt validatedAccessToken = (Jwt) authentication.getPrincipal();

		if (!String.valueOf(id).equals(validatedAccessToken.getClaimAsString("userId"))) {
			return false;
		}

		return true;
	}

	public static ResponseEntity<Response> deny(HttpServletRequest request) {

		Response response = Response.builder()
				.isError(true)
				.error(Error.builder()
						.id(UUID.randomUUID().getMostSignificantBits())
						.cause(SecurityResponses.USER_ID_NO_MATCH)
						.origin(UserSecurity.class.getSimpleName())
						.path(request.getPathInfo())
						.logged(false)
						.fatal(false)
						.build())
				.build();

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}
}
