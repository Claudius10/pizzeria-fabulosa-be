package org.pizzeria.fabulosa.web.util.constant;

public final class ValidationRules {

	public static final String SIMPLE_LETTERS_ONLY_MAX_25_INSENSITIVE_REQUIERED = "^[a-zA-Z\sÁÉÍÓÚáéíóúÑñ.-]{1,25}$";

	public static final String SIMPLE_LETTERS_ONLY_MAX_50_INSENSITIVE_REQUIERED = "^[a-zA-Z\sÁÉÍÓÚáéíóúÑñ.-]{2,50}$";

	public static final String COMPLEX_LETTERS_NUMBERS_MAX_150_OPTIONAL = "^[a-zA-Z0-9ÁÉÍÓÚáéíóúÑñ!¡¿?.-:,ºª\"\s]{0,150}$";

	public static final String USER_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";

	private ValidationRules() {
	}
}