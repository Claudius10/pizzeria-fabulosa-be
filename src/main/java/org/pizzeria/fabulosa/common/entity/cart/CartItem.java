package org.pizzeria.fabulosa.common.entity.cart;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.pizzeria.fabulosa.web.error.constraints.annotation.DoubleLength;
import org.pizzeria.fabulosa.web.error.constraints.annotation.IntegerLength;
import org.pizzeria.fabulosa.web.util.constant.ValidationResponses;

import java.util.List;
import java.util.Map;

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

	private String type;

	@Type(JsonType.class)
	@Column(columnDefinition = "json")
	private Map<String, String> name;

	@Type(JsonType.class)
	@Column(columnDefinition = "json")
	private Map<String, List<String>> description;

	@Type(JsonType.class)
	@Column(columnDefinition = "json")
	private Map<String, Map<String, String>> formats; // <"m", <"en": "Medium">, <"es": "Mediana">; "l", <"en": "Familiar">,
	// <"es": "Familiar">>

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
}
