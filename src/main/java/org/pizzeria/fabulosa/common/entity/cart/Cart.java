package org.pizzeria.fabulosa.common.entity.cart;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.pizzeria.fabulosa.common.entity.order.Order;
import org.pizzeria.fabulosa.web.dto.order.CartItemDTO;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "Cart")
@Table(name = "cart")
@Getter
@Setter
public class Cart {

	@Id
	private Long id;

	private Integer totalQuantity;

	private Double totalCost;

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

		public Builder withCartItems(List<CartItemDTO> cartItems) {
			cart.cartItems = new ArrayList<>();
			for (CartItemDTO item : cartItems) {
				cart.addItem(CartItem.builder()
						.withDescription(item.description())
						.withPrice(item.price())
						.withQuantity(item.quantity())
						.withFormats(item.formats())
						.withType(item.type())
						.withName(item.name())
						.build());
			}
			return this;
		}

		public Cart build() {
			return cart;
		}
	}
}