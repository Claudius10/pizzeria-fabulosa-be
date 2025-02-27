package org.pizzeria.fabulosa.configs.web.security.access.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.configs.properties.SecurityProperties;
import org.pizzeria.fabulosa.configs.web.security.auth.JWTTokenManager;
import org.pizzeria.fabulosa.configs.web.security.utils.SecurityCookieUtils;
import org.pizzeria.fabulosa.entity.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidLoginHandler implements AuthenticationSuccessHandler {

	private final JWTTokenManager jwtTokenManager;

	private final SecurityProperties securityProperties;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		String accessToken = jwtTokenManager.getAccessToken(user.getEmail(), user.getRoles(), user.getId());
		String idToken = jwtTokenManager.getIdToken(user.getEmail(), user.getName(), user.getId(), user.getContactNumber());
		SecurityProperties.Cookies cookies = securityProperties.getCookies();
		SecurityCookieUtils.serveCookies(response, accessToken, idToken, cookies.getSameSite(), cookies.getDomain());
	}
}