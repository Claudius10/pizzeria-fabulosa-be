package org.pizzeria.fabulosa.entity.role;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

@Entity(name = "Role")
@Table(name = "role")
public class Role implements GrantedAuthority {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_generator")
	@SequenceGenerator(name = "role_generator", sequenceName = "role_seq", allocationSize = 1)
	private Long id;

	@Column(unique = true)
	private String name;

	public Role() {
		// The JPA specification requires all Entity classes to have a default no-arg constructor.
	}

	public Role(String name) {
		this.name = name;
	}

	@Override
	public String getAuthority() {
		return this.name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
