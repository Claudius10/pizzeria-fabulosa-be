package org.pizzeria.fabulosa.configs.web.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.pizzeria.fabulosa.utils.Constants;
import org.pizzeria.fabulosa.utils.ServerUtils;
import org.pizzeria.fabulosa.web.constants.ApiRoutes;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class CookielessRequestFilter extends OncePerRequestFilter {

	/**
	 * If requested path is a path that requieres a cookie, but no cookie is present, then dismiss.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String path = ServerUtils.resolvePath(request.getServletPath(), request.getRequestURI());

		if (path == null) {
			filterChain.doFilter(request, response);
		} else {
			if (path.contains(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE) && !request.getMethod().equals("OPTIONS")) {

				if (null != request.getCookies()) {
					boolean containsAuthCookie = Arrays.stream(request.getCookies()).anyMatch(cookie ->
							cookie.getName().equals(Constants.AUTH_TOKEN));

					if (containsAuthCookie) {
						filterChain.doFilter(request, response);
					} else {
						log.warn("Found no cookies for path -> {}", path);
						response.sendError(HttpServletResponse.SC_NOT_FOUND);
					}
				} else {
					log.warn("Found no cookies for path -> {}", path);
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
				}
			} else {
				filterChain.doFilter(request, response);
			}
		}
	}
}
