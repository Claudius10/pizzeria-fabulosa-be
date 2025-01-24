package org.pizzeria.fabulosa.configs.web.security.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pizzeria.fabulosa.utils.Constants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

public final class SecurityCookieUtils {

	private SecurityCookieUtils() {
	}

	public static Cookie prepareCookie(String name, String value, int maxAge, boolean httpOnly, boolean secure) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);
		cookie.setHttpOnly(httpOnly);
		cookie.setSecure(secure);
		return cookie;
	}

	public static ResponseCookie bakeCookie(String name, String value, int maxAge, boolean httpOnly, boolean secure) {
		return ResponseCookie.from(name, value)
				.path("/")
				.maxAge(maxAge)
				.httpOnly(httpOnly)
				.secure(secure)
				.sameSite("Lax")
				//.domain("up.railway.app") // NOTE - on for prod fe
				.build();
	}

	public static void serveCookies(HttpServletResponse response, String accessToken, String idToken) {
		// access token cookie
		response.addHeader(HttpHeaders.SET_COOKIE,
				bakeCookie("token", accessToken, Constants.ONE_DAY_MS, true,
						false) // NOTE - true for prod
						.toString());

		// id token cookie
		response.addHeader(HttpHeaders.SET_COOKIE,
				bakeCookie("idToken", idToken, Constants.ONE_DAY_MS, false,
						false) // NOTE - true for prod
						.toString());
	}

	public static void eatAllCookies(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
			for (Cookie cookie : cookies) {
				//cookie.setSecure(true); // NOTE - on for prod
				//cookie.setDomain("up.railway.app"); // NOTE - on for prod
				cookie.setValue("");
				cookie.setPath("/");
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
	}
}