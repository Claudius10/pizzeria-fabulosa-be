package org.pizzeria.fabulosa.utils;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.pizzeria.fabulosa.web.dto.api.Response;

public final class ExceptionLogger {

	public static void log(Exception e, org.slf4j.Logger log, Response response) {
		log.error("Exception occurred!");
		log.warn("----- -- ExceptionLogger START ---- -- ");

		String simpleName = e.getClass().getSimpleName();
		String message = e.getMessage();
		Throwable cause = e.getCause();
		StackTraceElement[] stackTrace = e.getStackTrace();

		log.info("Exception simple name: {}", simpleName);

		if (message != null) {
			log.info("Exception message: {}", message);
		} else {
			log.info("Exception message: null");
		}

		if (cause != null) {
			log.info("Exception cause: {}", cause.getMessage());
		} else {
			log.info("Exception cause: null");
		}

		if (stackTrace.length > 0) {
			log.info("Exception stacktrace: {}", ExceptionUtils.getStackTrace(e));
		} else {
			log.info("Exception stacktrace: empty");
		}

		if (response != null) {
			log.info("Exception Response: {}", response);
		} else {
			log.info("Exception Response: null");
		}

		log.warn("----- -- ExceptionLogger END ---- -- ");
	}
}
