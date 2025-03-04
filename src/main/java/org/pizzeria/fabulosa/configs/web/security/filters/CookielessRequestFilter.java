package org.pizzeria.fabulosa.configs.web.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.pizzeria.fabulosa.utils.Constants;
import org.pizzeria.fabulosa.web.constants.ApiRoutes;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class CookielessRequestFilter extends OncePerRequestFilter {

	private final AuthenticationEntryPoint authenticationEntryPoint;

	public CookielessRequestFilter(AuthenticationEntryPoint authenticationEntryPoint) {
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			String path = resolvePath(request.getServletPath(), request.getRequestURI());

			if (path.contains(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE)) {

				if (null == request.getCookies()) {
					log.warn("CookielessRequestFilter found no cookies");
					goodbye(request, response);
				} else {
					boolean containsAuthCookie = Arrays.stream(request.getCookies()).anyMatch(cookie ->
							cookie.getName().equals(Constants.AUTH_TOKEN));

					if (containsAuthCookie) {
						filterChain.doFilter(request, response);
					} else {
						log.warn("CookielessRequestFilter did not find {}", Constants.AUTH_TOKEN);
						goodbye(request, response);
					}
				}
			} else {
				filterChain.doFilter(request, response);
			}
		} catch (RuntimeException e) {
			log.warn("CookielessRequestFilter does not have enough information to proceed");
			filterChain.doFilter(request, response);
		}
	}

	private String resolvePath(String one, String two) {
		boolean isOneInvalid = (null == one || one.isBlank());
		boolean isTwoInvalid = (null == two || two.isBlank());

		if (isOneInvalid && isTwoInvalid) {
			throw new RuntimeException();
		}

		if (isOneInvalid) {
			logPath(two);
			return two;
		}

		logPath(one);
		return one;
	}

	private void logPath(String path) {
		log.info("Filtered --> {}", path);
	}

	private void goodbye(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = String.format("Request rejected: cannot find cookies to attempt authentication for path: %s",
				request.getPathInfo());
		authenticationEntryPoint.commence(request, response, new AuthenticationCredentialsNotFoundException(message));
	}
}
