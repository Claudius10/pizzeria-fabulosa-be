package org.pizzeria.fabulosa.common.util.logger;

import org.pizzeria.fabulosa.web.dto.api.ResponseDTO;

public final class ExceptionLogger {

	public static void log(Exception e, org.slf4j.Logger log, ResponseDTO response) {
		log.warn("----- -- Exception caught ---- -- ");
		log.warn("----- --");
		log.warn("----- -- ExceptionLogger START ---- -- ");
		log.warn("----- --");

		String simpleName = e.getClass().getSimpleName();
		String message = e.getMessage();
		Throwable cause = e.getCause();
		StackTraceElement[] stackTrace = e.getStackTrace();

		log.warn("----- -- ExceptionLogger details ---- -- ");
		log.info("Exception simple name: {}", simpleName);

		if (message != null) {
			log.info("Exception message: {}", message);
		}

		if (cause != null) {
			log.info("Exception cause: {}", cause.getMessage());
		} else {
			log.info("Exception cause: null");
		}

		if (response != null) {
			log.info("Exception Response: {}", response);
		}

		log.warn("----- --");
		log.warn("----- -- ExceptionLogger stack trace ---- -- ");

		if (stackTrace.length > 0) {
			log.info("Exception", e);
		}

		log.warn("----- --");
		log.warn("----- -- ExceptionLogger END ---- -- ");
	}
}
