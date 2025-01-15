package org.pizzeria.fabulosa.entity.cart;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.pizzeria.fabulosa.web.constants.ValidationResponses;
import org.pizzeria.fabulosa.web.exceptions.constraints.annotation.DoubleLength;
import org.pizzeria.fabulosa.web.exceptions.constraints.annotation.IntegerLength;

@Entity(name = "CartItem")
@Table(name = "cart_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class CartItem {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_item_generator")
	@SequenceGenerator(name = "cart_item_generator", sequenceName = "cart_item_seq", allocationSize = 1)
	private Long id;

	private String code;

	private String format;

	@DoubleLength(min = 1, max = 5)
	private Double price;

	@IntegerLength(min = 1, max = 2, message = ValidationResponses.CART_ITEM_MAX_QUANTITY_ERROR)
	private Integer quantity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference
	private Cart cart;

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof CartItem))
			return false;

		return id != null && id.equals(((CartItem) obj).getId());
	}

//	public boolean contentEquals(Object o) {
//		CartItem cartItem = (CartItem) o;
//		return Objects.equals(code, cartItem.code)
//				&& Objects.equals(quantity, cartItem.quantity)
//				&& Objects.equals(price, cartItem.price);
//	}
}
