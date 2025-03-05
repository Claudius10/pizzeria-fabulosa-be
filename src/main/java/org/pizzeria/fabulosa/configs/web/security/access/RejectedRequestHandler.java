package org.pizzeria.fabulosa.configs.web.security.access;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pizzeria.fabulosa.entity.error.Error;
import org.pizzeria.fabulosa.repos.error.ErrorRepository;
import org.pizzeria.fabulosa.utils.ServerUtils;
import org.pizzeria.fabulosa.utils.loggers.ExceptionLogger;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.security.web.firewall.RequestRejectedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Component
public class RejectedRequestHandler implements RequestRejectedHandler {

	private final ErrorRepository errorRepository;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, RequestRejectedException ex) throws IOException {

		String path = ServerUtils.resolvePath(request.getServletPath(), request.getRequestURI());
		String completePath = String.format("URL %s URI %s", request.getRequestURL(), path);

		Error error = Error.builder()
				.createdOn(LocalDateTime.now())
				.cause(ex.getClass().getSimpleName())
				.message(ex.getMessage())
				.origin(getClass().getName())
				.path(completePath)
				.logged(true)
				.fatal(true)
				.build();

		this.errorRepository.save(error);

		ExceptionLogger.log(ex, log, null);
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}
}
