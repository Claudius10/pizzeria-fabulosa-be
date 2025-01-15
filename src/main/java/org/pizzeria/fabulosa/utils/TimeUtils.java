package org.pizzeria.fabulosa.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public final class TimeUtils {

	private static final int CREATED_ON_PLUS_ONE_HOUR = 1;

	private static final int CREATED_ON_PLUS_ONE_TWO = 2;

	private TimeUtils() {
		// do not init
	}

	public static boolean isDst() {
		return TimeZone.getTimeZone("Europe/Paris").inDaylightTime(new Date());
	}

	public static String getNowAsStringAccountingDST() {
		int plusHours = isDst() ? CREATED_ON_PLUS_ONE_TWO : CREATED_ON_PLUS_ONE_HOUR;
		return LocalDateTime.now().plusHours(plusHours).format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy"));
	}
}