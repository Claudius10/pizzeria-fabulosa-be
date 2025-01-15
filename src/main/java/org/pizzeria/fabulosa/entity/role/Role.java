package org.pizzeria.fabulosa.entity.role;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

@Entity(name = "Role")
@Table(name = "role")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class Role implements GrantedAuthority {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_generator")
	@SequenceGenerator(name = "role_generator", sequenceName = "role_seq", allocationSize = 1)
	private Long id;

	@Column(unique = true)
	private String name;

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
