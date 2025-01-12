package org.pizzeria.fabulosa.entity.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.pizzeria.fabulosa.web.constants.ValidationResponses;
import org.pizzeria.fabulosa.web.constants.ValidationRules;
import org.pizzeria.fabulosa.web.exceptions.constraints.annotation.DoubleLengthNullable;

import java.util.Objects;

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

	@NotBlank(message = ValidationResponses.ORDER_DETAILS_DELIVERY_HOUR)
	private String deliveryTime;

	@NotBlank(message = ValidationResponses.ORDER_DETAILS_PAYMENT)
	private String paymentMethod;

	@DoubleLengthNullable(min = 0, max = 5, message = ValidationResponses.ORDER_DETAILS_BILL)
	private Double billToChange;

	private Double changeToGive;

	@Pattern(regexp = ValidationRules.COMPLEX_LETTERS_NUMBERS_MAX_150_OPTIONAL, message = ValidationResponses.ORDER_DETAILS_COMMENT)
	private String comment;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JsonBackReference
	private Order order;

	public boolean contentEquals(Object o) {
		OrderDetails that = (OrderDetails) o;
		return Objects.equals(deliveryTime, that.deliveryTime)
				&& Objects.equals(paymentMethod, that.paymentMethod)
				&& Objects.equals(billToChange, that.billToChange)
				&& Objects.equals(comment, that.comment);
	}
}