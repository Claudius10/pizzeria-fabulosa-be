package org.pizzeria.fabulosa.common.entity.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.pizzeria.fabulosa.web.dto.order.OrderDetailsDTO;

@Entity(name = "OrderDetails")
@Table(name = "order_details")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(setterPrefix = "with")
public class OrderDetails {

	@Id
	private Long id;

	private String deliveryTime;

	private String paymentMethod;

	private Double billToChange;

	private Double changeToGive;

	private String comment;

	private Boolean storePickUp;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JsonBackReference
	private Order order;

	public static FromDTO fromDTOBuilder() {
		return new FromDTO();
	}

	public static class FromDTO {
		private FromDTO() {
		}

		public OrderDetails build(OrderDetailsDTO dto) {
			return OrderDetails.builder()
					.withPaymentMethod(dto.paymentMethod())
					.withDeliveryTime(dto.deliveryTime())
					.withComment(dto.comment())
					.withStorePickUp(dto.storePickUp())
					.withBillToChange(dto.billToChange())
					.build();
		}
	}
}