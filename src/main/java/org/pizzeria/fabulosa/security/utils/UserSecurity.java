package org.pizzeria.fabulosa.security.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.pizzeria.fabulosa.common.entity.error.APIError;
import org.pizzeria.fabulosa.common.util.TimeUtils;
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

	/**
	 * Checks if the provided user id as a PathVariable matches the stored user id in the Access Token.
	 *
	 * @param id to check
	 * @return true if matched, false otherwise
	 */
	public static boolean valid(Long id) {
		if (id == null) {
			return false;
		}

		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		Jwt validatedAccessToken = (Jwt) authentication.getPrincipal();

		return String.valueOf(id).equals(validatedAccessToken.getClaimAsString("userId"));
	}

	public static ResponseEntity<Response> deny(HttpServletRequest request) {

		Response response = Response.builder()
				.apiError(APIError.builder()
						.id(UUID.randomUUID().getMostSignificantBits())
						.createdOn(TimeUtils.getNowAccountingDST())
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
