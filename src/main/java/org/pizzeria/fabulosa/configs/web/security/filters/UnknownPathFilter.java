package org.pizzeria.fabulosa.configs.web.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.pizzeria.fabulosa.utils.ServerUtils;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Slf4j
public class UnknownPathFilter extends OncePerRequestFilter {

	private final AuthenticationEntryPoint authenticationEntryPoint;

	public UnknownPathFilter(AuthenticationEntryPoint authenticationEntryPoint) {
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			String path = ServerUtils.resolvePath(request.getServletPath(), request.getRequestURI());

			if (isPathKnown(path)) {
				filterChain.doFilter(request, response);
			} else {
				log.warn("UnknownPathFilter rejected --> {}", path);
				goodbye(request, response);
			}

		} catch (RuntimeException e) {
			log.warn("UnknownPathFilter does not have enough information to proceed");
			filterChain.doFilter(request, response);
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

	private void goodbye(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		authenticationEntryPoint.commence(request, response, new InternalAuthenticationServiceException("Unmapped path"));
	}
}
