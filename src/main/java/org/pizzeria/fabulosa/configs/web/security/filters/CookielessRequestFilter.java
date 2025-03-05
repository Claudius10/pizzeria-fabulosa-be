package org.pizzeria.fabulosa.configs.web.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.pizzeria.fabulosa.utils.Constants;
import org.pizzeria.fabulosa.utils.ServerUtils;
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
			String path = ServerUtils.resolvePath(request.getServletPath(), request.getRequestURI());
			log.info("Filtering --> {}", path);

			if (path.contains(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE)) {

				if (null == request.getCookies()) {
					log.warn("Found no cookies");
					goodbye(request, response);
				} else {
					boolean containsAuthCookie = Arrays.stream(request.getCookies()).anyMatch(cookie ->
							cookie.getName().equals(Constants.AUTH_TOKEN));

					if (containsAuthCookie) {
						filterChain.doFilter(request, response);
					} else {
						log.warn("Did not find {}", Constants.AUTH_TOKEN);
						goodbye(request, response);
					}
				}
			} else {
				filterChain.doFilter(request, response);
			}
		} catch (RuntimeException e) {
			log.warn("Not have enough information to proceed");
			filterChain.doFilter(request, response);
		}
	}

	private void goodbye(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		authenticationEntryPoint.commence(request, response,
				new AuthenticationCredentialsNotFoundException("Missing AUTH_TOKEN"));
	}
}
