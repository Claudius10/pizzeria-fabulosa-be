package org.pizzeria.fabulosa.security.access;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.security.utils.SecurityCookies;
import org.pizzeria.fabulosa.web.property.SecurityProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClearCookiesLogoutHandler implements LogoutHandler {

	private final SecurityProperties securityProperties;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		SecurityCookies.eatAllCookies(request, response, securityProperties.getCookies().getDomain());
	}
}