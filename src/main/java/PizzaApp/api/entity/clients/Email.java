package PizzaApp.api.entity.clients;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

@Entity(name = "Email")
@Table(name = "email")
public class Email {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "email")
	@jakarta.validation.constraints.Email(message = "Email: formato del email no aceptado")
	@NotBlank(message = "Email: el valor no puede ser vacío")
	private String email;

	public Email() {
	}

	private Email(Builder builder) {
		this.id = builder.id;
		this.email = builder.email;
	}

	public static class Builder {
		private Long id;
		private String email;

		public Builder() {
		}

		public Builder withId(long id) {
			this.id = id;
			return this;
		}

		public Builder withEmail(String email) {
			this.email = email;
			return this;
		}

		public Email build() {
			return new Email(this);
		}
	}

	public Email(String email) {
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Email [id=" + id + ", email=" + email + "]";
	}

	public boolean entityEquals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Email email1 = (Email) o;
		return Objects.equals(email, email1.email);
	}
}