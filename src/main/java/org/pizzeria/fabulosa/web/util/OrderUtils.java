package org.pizzeria.fabulosa.web.util;

import org.pizzeria.fabulosa.common.entity.dto.CreatedOrderDTO;
import org.pizzeria.fabulosa.common.entity.order.Order;
import org.pizzeria.fabulosa.web.dto.order.*;

public final class OrderUtils {

	// changeRequested == null || (changeRequested - totalCostOffers || totalCost)
	public static Double calculatePaymentChange(Double changeRequested, Double totalCost, Double totalCostAfterOffers) {
		if (changeRequested == null) {
			return null;
		}

		if (totalCostAfterOffers > 0) {
			return (double) Math.round((changeRequested - totalCostAfterOffers) * 100) / 100;
		}

		return (double) Math.round((changeRequested - totalCost) * 100) / 100;
	}

	public static CreatedOrderDTO fromOrderToDTO(Order order) {
		return new CreatedOrderDTO(
				order.getId(),
				order.getFormattedCreatedOn(),
				new CustomerDTO(
						order.getAnonCustomerName() == null ? order.getUser().getName() : order.getAnonCustomerName(),
						order.getAnonCustomerContactNumber() == null ? order.getUser().getContactNumber() : order.getAnonCustomerContactNumber(),
						order.getAnonCustomerEmail() == null ? order.getUser().getEmail() : order.getAnonCustomerEmail()
				),
				new AddressDTO(
						order.getAddress().getId(),
						order.getAddress().getStreet(),
						order.getAddress().getNumber(),
						order.getAddress().getDetails()
				),
				new OrderDetailsDTO(
						order.getOrderDetails().getDeliveryTime(),
						order.getOrderDetails().getPaymentMethod(),
						order.getOrderDetails().getBillToChange(),
						order.getOrderDetails().getComment(),
						order.getOrderDetails().getStorePickUp(),
						order.getOrderDetails().getChangeToGive()
				),
				new CartDTO(
						order.getCart().getTotalQuantity(),
						order.getCart().getTotalCost(),
						order.getCart().getTotalCostOffers(),
						order.getCart().getCartItems().stream().map(cartItem ->
										new CartItemDTO(
												cartItem.getType(),
												cartItem.getPrice(),
												cartItem.getQuantity(),
												cartItem.getName(),
												cartItem.getDescription(),
												cartItem.getFormats()))
								.toList())
		);
	}
}
