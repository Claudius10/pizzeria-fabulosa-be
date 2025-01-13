package org.pizzeria.fabulosa.entity.order;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.pizzeria.fabulosa.entity.address.Address;
import org.pizzeria.fabulosa.entity.cart.Cart;
import org.pizzeria.fabulosa.entity.user.User;

import java.time.LocalDateTime;

@Entity(name = "Order")
@Table(name = "orders")
@Getter
@Setter
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_generator")
	@SequenceGenerator(name = "order_generator", sequenceName = "order_seq", allocationSize = 1)
	private Long id;

	private LocalDateTime createdOn;

	private LocalDateTime updatedOn;

	private String formattedCreatedOn;

	private String formattedUpdatedOn;

	private String anonCustomerName;

	private Integer anonCustomerContactNumber;

	private String anonCustomerEmail;

	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
			fetch = FetchType.LAZY)
	private Address address;

	@OneToOne(mappedBy = "order", cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
	@JsonManagedReference
	private OrderDetails orderDetails;

	// NOTE - bidirectional OneToOne association's non-owning side
	//  can only be lazy fetched if the association is never null ->
	//  optional = false
	@OneToOne(mappedBy = "order", cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
	@JsonManagedReference
	private Cart cart;

	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.SET_NULL)
	private User user;

	public Order() {
		// The JPA specification requires all Entity classes to have a default no-arg constructor.
	}

	public void setOrderDetails(OrderDetails orderDetails) {
		if (orderDetails == null) {
			if (this.orderDetails != null) {
				this.orderDetails.setOrder(null);
			}
		} else {
			orderDetails.setOrder(this);
		}
		this.orderDetails = orderDetails;
	}

	public void setCart(Cart cart) {
		if (cart == null) {
			if (this.cart != null) {
				this.cart.setOrder(null);
			}
		} else {
			cart.setOrder(this);
		}
		this.cart = cart;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof Order))
			return false;

		return id != null && id.equals(((Order) obj).getId());
	}

	public static class Builder {

		private final Order order;

		public Builder() {
			order = new Order();
		}

		public Builder withCreatedOn(LocalDateTime createdOn) {
			order.createdOn = createdOn;
			return this;
		}

		public Builder withFormattedCreatedOn(String formattedCreatedOn) {
			order.formattedCreatedOn = formattedCreatedOn;
			return this;
		}

		public Builder withAddress(Address address) {
			order.address = address;
			return this;
		}

		public Builder withOrderDetails(OrderDetails orderDetails) {
			order.setOrderDetails(orderDetails);
			return this;
		}

		public Builder withCart(Cart cart) {
			order.setCart(cart);
			return this;
		}

		public Builder withAnonCustomer(String anonCustomerName, Integer anonCustomerContactNumber, String anonCustomerEmail) {
			order.anonCustomerName = anonCustomerName;
			order.anonCustomerContactNumber = anonCustomerContactNumber;
			order.anonCustomerEmail = anonCustomerEmail;
			order.user = null;
			return this;
		}

		public Builder withUser(User user) {
			order.anonCustomerName = null;
			order.anonCustomerContactNumber = null;
			order.anonCustomerEmail = null;
			order.user = user;
			return this;
		}

		public Order build() {
			return order;
		}
	}
}