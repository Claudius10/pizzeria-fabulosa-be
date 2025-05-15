package org.pizzeria.fabulosa.common.entity.cart;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.pizzeria.fabulosa.common.entity.order.Order;
import org.pizzeria.fabulosa.web.error.constraints.annotation.DoubleLength;
import org.pizzeria.fabulosa.web.error.constraints.annotation.DoubleLengthNullable;
import org.pizzeria.fabulosa.web.error.constraints.annotation.IntegerLength;
import org.pizzeria.fabulosa.web.util.constant.ValidationResponses;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "Cart")
@Table(name = "cart")
@Getter
@Setter
public class Cart {

	@Id
	private Long id;

	@IntegerLength(min = 1, max = 2, message = ValidationResponses.CART_MAX_PRODUCTS_QUANTITY_ERROR)
	private Integer totalQuantity;

	@DoubleLength(min = 1, max = 6, message = "")
	private Double totalCost;

	@DoubleLengthNullable(min = 0, max = 6, message = "")
	private Double totalCostOffers;

	// INFO to remember about the Cart/CartItem association:
	// given that Order & Cart association has CascadeType.ALL
	// and Cart & CartItem bidirectional association also has CascadeType.ALL
	// when updating Cart, the merge operation is going to be cascaded to the
	// CartItem association as well, so there's no need to manually
	// sync the bidirectional association
	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JsonManagedReference
	private List<CartItem> cartItems;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JsonBackReference
	private Order order;

	public Cart() {
		// The JPA specification requires all Entity classes to have a default no-arg constructor.
	}

	public void addItem(CartItem item) {
		item.setId(null);
		cartItems.add(item);
		item.setCart(this);
	}

//	public void removeItem(CartItem item) {
//		cartItems.remove(item);
//		item.setCart(null);
//	}

//	public boolean contentEquals(Object o) {
//		if (this == o) return true;
//		if (o == null || getClass() != o.getClass()) return false;
//		Cart cart = (Cart) o;
//
//		// orderItem contentEquals
//		List<Boolean> itemEqualityCheck = new ArrayList<>();
//		for (int i = 0; i < cartItems.size(); i++) {
//			for (int j = 0; j < cart.getCartItems().size(); j++) {
//
//				if (cartItems.get(i).contentEquals(cart.cartItems.get(j))) {
//					itemEqualityCheck.add(true);
//
//					// avoid i value becoming greater than orderItems.size() value
//					if (i < cartItems.size() - 1) {
//						// move to next i if i0 is equal to j0
//						// to avoid comparing i0 to j1
//						i++;
//					}
//				} else {
//					itemEqualityCheck.add(false);
//				}
//			}
//		}
//
//		boolean areItemsEqual = true;
//		for (Boolean bool : itemEqualityCheck) {
//			if (!bool) {
//				areItemsEqual = false;
//				break;
//			}
//		}
//
//		return Objects.equals(totalQuantity, cart.totalQuantity) &&
//				Objects.equals(totalCost, cart.totalCost) &&
//				Objects.equals(totalCostOffers, cart.totalCostOffers) &&
//				areItemsEqual;
//	}

	public static class Builder {

		private final Cart cart;

		public Builder() {
			this.cart = new Cart();
		}

		public Builder withTotalQuantity(Integer totalQuantity) {
			cart.totalQuantity = totalQuantity;
			return this;
		}

		public Builder withTotalCost(Double totalCost) {
			cart.totalCost = totalCost;
			return this;
		}

		public Builder withTotalCostOffers(Double totalCostOffers) {
			cart.totalCostOffers = totalCostOffers;
			return this;
		}

		public Builder withCartItems(List<CartItem> cartItems) {
			cart.cartItems = new ArrayList<>();
			for (CartItem item : cartItems) {
				item.setId(null);
				cart.addItem(item);
			}
			return this;
		}

		public Builder withEmptyItemList() {
			cart.cartItems = new ArrayList<>();
			cart.totalQuantity = 0;
			return this;
		}

		public Cart build() {
			return cart;
		}
	}
}