package org.pizzeria.fabulosa.web.util.constant;

public final class ApiRoutes {

	// BASE

	public static final String BASE = "/api";

	public static final String V1 = "/v1";

	public static final String ALL = "/**";

	// DOCS

	public static final String DOCS = "/docs";

	// AUTH

	public static final String AUTH_BASE = "/auth";

	public static final String AUTH_LOGIN = "/login";

	public static final String AUTH_LOGOUT = "/logout";

	public static final String AUTH_LOGIN_EMAIL = "/logout";

	// ANON

	public static final String ANON_BASE = "/anon";

	public static final String ANON_REGISTER = "/register";

	public static final String ANON_ORDER = "/order";

	// RESOURCE

	public static final String RESOURCE_BASE = "/resource";

	public static final String RESOURCE_PRODUCT = "/product";

	public static final String RESOURCE_PRODUCT_PARAM = "type";

	public static final String RESOURCE_STORE = "/store";

	public static final String RESOURCE_STORE_ID = "/{storeId}";

	public static final String RESOURCE_OFFER = "/offer";

	public static final String LOCAL_DATE_TIME_NOW = "/now";

	// USER

	public static final String USER_BASE = "/user";

	public static final String USER_ID = "/{userId}";
	public static final String USER_ORDER = "/order";
	public static final String USER_ADDRESS_ID = "/{addressId}";

	public static final String USER_ADDRESS = "/address";
	public static final String USER_EMAIL = "/email";
	public static final String USER_NAME = "/name";
	public static final String USER_NUMBER = "/number";
	public static final String USER_PASSWORD = "/password";

	// ORDER

	public static final String ORDER_BASE = "/order";

	public static final String ORDER_ID = "/{orderId}";

	public static final String ORDER_SUMMARY = "/summary";

	// MISC

	public static final String PAGE_NUMBER = "pageNumber";
	public static final String PAGE_SIZE = "pageSize";

	private ApiRoutes() {
		// no init
	}
}
