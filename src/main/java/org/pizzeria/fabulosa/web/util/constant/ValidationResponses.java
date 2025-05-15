package org.pizzeria.fabulosa.web.util.constant;

public final class ValidationResponses {

	public static final String NAME_INVALID = "InvalidCustomerName";

	public static final String EMAIL_INVALID = "InvalidCustomerEmail";

	public static final String EMAIL_NO_MATCH = "NoMatchEmail";

	public static final String PASSWORD_INVALID = "InvalidPassword";

	public static final String PASSWORD_NO_MATCH = "NoMatchPassword";

	public static final String NUMBER_INVALID = "InvalidCustomerNumber";

	public static final String ADDRESS_STREET = "InvalidAddressStreet";

	public static final String ADDRESS_STREET_NUMBER = "InvalidAddressStreetNumber";

	public static final String ADDRESS_DETAILS = "InvalidAddressDetails";

	public static final String ORDER_DETAILS_DELIVERY_HOUR = "InvalidOrderDetailsDeliveryHour";

	public static final String ORDER_DETAILS_PAYMENT = "InvalidOrderDetailsPayment";

	public static final String ORDER_DETAILS_BILL = "InvalidOrderDetailsBillToChange";

	public static final String ORDER_DETAILS_COMMENT = "InvalidOrderDetailsComment";

	public static final String CART_IS_EMPTY = "InvalidCartIsEmpty";

	public static final String ORDER_DELETE_TIME_ERROR = "InvalidOrderDeleteTime";

	public static final String CART_MAX_PRODUCTS_QUANTITY_ERROR = "InvalidCartMaxProductsQuantity";

	public static final String CART_ITEM_MAX_QUANTITY_ERROR = "InvalidCartItemMaxQuantity";

	private ValidationResponses() {
	}
}