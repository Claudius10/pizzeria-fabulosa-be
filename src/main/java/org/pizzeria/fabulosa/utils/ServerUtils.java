package org.pizzeria.fabulosa.utils;

public final class ServerUtils {

	private ServerUtils() {
	}

	public static String resolvePath(String one, String two) {
		boolean isOneInvalid = (null == one || one.isBlank());
		boolean isTwoInvalid = (null == two || two.isBlank());

		if (isOneInvalid && isTwoInvalid) {
			throw new RuntimeException("Erreur lors de l'resolution du serveur");
		}

		if (isOneInvalid) {
			return two;
		}

		return one;
	}
}
