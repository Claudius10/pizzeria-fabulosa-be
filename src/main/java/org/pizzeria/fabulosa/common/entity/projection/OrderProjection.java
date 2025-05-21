package org.pizzeria.fabulosa.common.entity.projection;

import org.pizzeria.fabulosa.common.entity.address.Address;
import org.pizzeria.fabulosa.common.entity.cart.Cart;
import org.pizzeria.fabulosa.common.entity.order.OrderDetails;

import java.time.LocalDateTime;

public interface OrderProjection {

	Long getId();

	LocalDateTime getCreatedOn();

	String getFormattedCreatedOn();

	Address getAddress();

	OrderDetails getOrderDetails();

	Cart getCart();
}
