package org.pizzeria.fabulosa.web.util.constant;

public final class ApiResponses {

	public static final String USER_EMAIL_ALREADY_EXISTS = "EmailAlreadyExists";

	public static final String ADDRESS_MAX_SIZE = "UserAddressListFull";

	public static final String ORDER_NOT_FOUND = "UserOrderNotFound";

	public static final String ADDRESS_NOT_FOUND = "UserAddressNotFound";

	public static final String DUMMY_ACCOUNT_ERROR = "DummyAccountError";

	public static final String USER_NOT_FOUND = "UserNotFound"; // when user does not exist in the database

	public static final String JSON = "application/json";
	public static final String TEXT = "text/plain";
	public static final String OK = "200";
	public static final String CREATED = "201";
	public static final String NO_CONTENT = "204";
	public static final String BAD_REQUEST = "400";
	public static final String UNAUTHORIZED = "401";
	public static final String INTERNAL_SERVER_ERROR = "500";
	public static final String DOUBLE_QUOTES = "\"";

	private ApiResponses() {
	}
}