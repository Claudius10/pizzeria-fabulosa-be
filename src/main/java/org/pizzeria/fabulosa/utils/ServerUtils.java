package org.pizzeria.fabulosa.utils;

import jakarta.servlet.http.HttpServletRequest;

public final class ServerUtils {

	private ServerUtils() {
	}

	public static String resolvePath(String one, String two) {
		boolean isOneInvalid = (null == one || one.isBlank());
		boolean isTwoInvalid = (null == two || two.isBlank());

		if (isOneInvalid && isTwoInvalid) {
			return null;
		}

		if (isOneInvalid) {
			return two;
		}

		return one;
	}

	public static void logRequest(HttpServletRequest request, org.slf4j.Logger log, String origin) {
		log.warn("----- -- Request START ---- -- ");
		log.warn("----- --> {}", origin);
		log.warn("----- --");

		log.info("----- {} : {}", "URL", request.getRequestURL());
		log.info("----- {} : {}", "URI", request.getRequestURI());
		log.info("----- {} : {}", "ServletPath", request.getServletPath());
		log.info("----- {} : {}", "ContextPath", request.getContextPath());
		log.info("-----");
		log.info("----- {} : {}", "Method", request.getMethod());
		log.info("----- {} : {}", "ContentType", request.getContentType());
		log.info("----- {} : {}", "Scheme", request.getScheme());
		log.info("----- {} : {}", "Protocol", request.getProtocol());
		log.info("-----");
		log.info("----- {} : {}", "ServerName", request.getServerName());
		log.info("----- {} : {}", "ServerPort", request.getServerPort());
		log.info("-----");
		log.info("----- {} : {}", "RemoteUser", request.getRemoteUser());
		log.info("----- {} : {}", "RemoteHost", request.getRemoteHost());
		log.info("----- {} : {}", "RemotePort", request.getRemotePort());
		log.info("-----");
		log.info("----- {} : {}", "LocalAddress", request.getLocalAddr());
		log.info("----- {} : {}", "LocalName", request.getLocalName());
		log.info("----- {} : {}", "LocalPort", request.getLocalPort());

		log.warn("----- --");
		log.warn("----- -- Request END ---- -- ");
	}
}
