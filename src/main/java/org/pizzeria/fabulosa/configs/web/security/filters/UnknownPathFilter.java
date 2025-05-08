package org.pizzeria.fabulosa.configs.web.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.pizzeria.fabulosa.utils.ServerUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Slf4j
public class UnknownPathFilter extends OncePerRequestFilter {

	/**
	 * Dismiss unmapped paths.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String path = ServerUtils.resolvePath(request.getServletPath(), request.getRequestURI());

		if (path == null) {
			log.error("Unable to resolve path for URL {}", request.getRequestURL());
			filterChain.doFilter(request, response);
		} else {
			if (isPathKnown(path)) {
				filterChain.doFilter(request, response);
			} else {
				log.warn("Rejected --> {}", path);
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
		}
	}

	private boolean isPathKnown(String path) {
		for (String knownPath : getKnownPaths()) {
			if (path.contains(knownPath)) {
				return true;
			}
		}

		return false;
	}

	private Set<String> getKnownPaths() {
		return Set.of(
				"/api/tests",
				"/api/v1/auth",
				"/api/v1/user",
				"/api/v1/resource",
				"/api/v1/anon"
		);
	}
}
