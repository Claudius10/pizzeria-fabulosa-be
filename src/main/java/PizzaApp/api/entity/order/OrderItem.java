package PizzaApp.api.entity.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity(name = "OrderItem")
@Table(name = "order_item")
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "product_type")
	private String productType;

	@Column(name = "name")
	private String name;

	@Column(name = "format")
	private String format;

	@Column(name = "quantity")
	private Integer quantity;

	@Column(name = "price")
	private Double price;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference
	private Cart cart;

	public OrderItem() {
	}

	private OrderItem(Builder builder) {
		this.id = builder.id;
		this.productType = builder.productType;
		this.name = builder.name;
		this.format = builder.format;
		this.quantity = builder.quantity;
		this.price = builder.price;
		this.cart = null;
	}

	public static class Builder {
		private Long id;
		private String productType;
		private String name;
		private String format;
		private Integer quantity;
		private Double price;

		public Builder() {
		}

		public Builder withId(Long id) {
			this.id = id;
			return this;
		}

		public Builder withProductType(String productType) {
			this.productType = productType;
			return this;
		}

		public Builder withWithName(String name) {
			this.name = name;
			return this;
		}

		public Builder withFormat(String format) {
			this.format = format;
			return this;
		}

		public Builder withQuantity(Integer quantity) {
			this.quantity = quantity;
			return this;
		}

		public Builder withPrice(Double price) {
			this.price = price;
			return this;
		}

		public OrderItem build() {
			return new OrderItem(this);
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
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

		if (!(obj instanceof OrderItem))
			return false;

		return id != null && id.equals(((OrderItem) obj).getId());
	}

	@Override
	public String toString() {
		return "OrderItem [id=" + id + ", productType=" + productType + ", name=" + name + ", format=" + format
				+ ", quantity=" + quantity + ", price=" + price + "]";
	}

	public boolean contentEquals(Object o) {
		OrderItem orderItem = (OrderItem) o;
		return Objects.equals(productType, orderItem.productType)
				&& Objects.equals(name, orderItem.name)
				&& Objects.equals(format, orderItem.format)
				&& Objects.equals(quantity, orderItem.quantity)
				&& Objects.equals(price, orderItem.price);
	}
}
