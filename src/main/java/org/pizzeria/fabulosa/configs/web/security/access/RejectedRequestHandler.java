package org.pizzeria.fabulosa.configs.web.security.access;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.pizzeria.fabulosa.utils.ServerUtils;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.security.web.firewall.RequestRejectedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RejectedRequestHandler implements RequestRejectedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, RequestRejectedException ex) throws IOException {
		String logMsg = "Rejected request: " + request.getRequestURL() + "URI: ";
		String path = ServerUtils.resolvePath(request.getServletPath(), request.getRequestURI());

		if (path == null) {
			log.warn(logMsg);
		} else {
			log.warn("{}{}", logMsg, path);
		}

		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}
}
