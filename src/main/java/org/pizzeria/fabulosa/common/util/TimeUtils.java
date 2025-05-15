package org.pizzeria.fabulosa.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public final class TimeUtils {

	private static final String DATE_FORMAT_PATTERN = "HH:mm - dd/MM/yyyy";

	private TimeUtils() {
		// do not init
	}

	public static boolean isDst() {
		return TimeZone.getTimeZone("Europe/Paris").inDaylightTime(new Date());
	}

	public static LocalDateTime getNowAccountingDST() {
		int plusHours = isDst() ? 2 : 1;
		return LocalDateTime.now().plusHours(plusHours);
	}

	public static String formatDateAsString(LocalDateTime date) {
		return date.format(DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN));
	}
}