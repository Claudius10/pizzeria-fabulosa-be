package org.pizzeria.fabulosa.security.access.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.common.entity.user.User;
import org.pizzeria.fabulosa.security.auth.JWTTokenManager;
import org.pizzeria.fabulosa.security.utils.SecurityCookies;
import org.pizzeria.fabulosa.web.property.SecurityProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import static org.pizzeria.fabulosa.common.util.constant.Constants.ONE_DAY_MS;
import static org.pizzeria.fabulosa.web.util.constant.SecurityConstants.AUTH_TOKEN_NAME;
import static org.pizzeria.fabulosa.web.util.constant.SecurityConstants.ID_TOKEN_NAME;

@Component
@RequiredArgsConstructor
public class ValidLoginHandler implements AuthenticationSuccessHandler {

	private final JWTTokenManager jwtTokenManager;

	private final SecurityProperties securityProperties;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		User user = (User) authentication.getPrincipal();

		String accessToken = jwtTokenManager.generateAccessToken(user.getEmail(), user.getRoles(), user.getId());
		String idToken = jwtTokenManager.generateIdToken(user.getEmail(), user.getName(), user.getId(), user.getContactNumber());

		SecurityProperties.Cookies cookies = securityProperties.getCookies();

		// auth token
		SecurityCookies.serveCookies(
				response,
				AUTH_TOKEN_NAME,
				accessToken,
				ONE_DAY_MS,
				cookies.getHttpOnly(),
				cookies.getSecure(),
				cookies.getSameSite(),
				cookies.getDomain());

		// id token
		SecurityCookies.serveCookies(
				response,
				ID_TOKEN_NAME,
				idToken,
				ONE_DAY_MS,
				false,
				cookies.getSecure(),
				cookies.getSameSite(),
				cookies.getDomain());
	}
}