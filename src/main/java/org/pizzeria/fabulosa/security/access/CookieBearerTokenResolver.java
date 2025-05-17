package org.pizzeria.fabulosa.security.access;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import static org.pizzeria.fabulosa.web.util.constant.SecurityConstants.ACCESS_TOKEN;

@Component
public final class CookieBearerTokenResolver implements BearerTokenResolver {

	@Override
	public String resolve(HttpServletRequest request) {
		Cookie accessToken = WebUtils.getCookie(request, ACCESS_TOKEN);
		if (accessToken != null) {
			return accessToken.getValue();
		}
		return null;
	}
}