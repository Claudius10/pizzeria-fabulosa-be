package org.pizzeria.fabulosa.common.entity.user;

import jakarta.persistence.*;
import lombok.*;
import org.pizzeria.fabulosa.common.entity.address.Address;
import org.pizzeria.fabulosa.common.entity.role.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Entity(name = "User")
@Table(name = "user",
		uniqueConstraints = @UniqueConstraint(name = "USER_EMAIL", columnNames = "email")
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
	@SequenceGenerator(name = "user_generator", sequenceName = "user_seq", allocationSize = 1)
	private Long id;

	private String name;

	private String email;

	private Integer contactNumber;

	private String password;

	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JoinTable(name = "users_addresses",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "address_id"))
	private Set<Address> addressList;

	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JoinTable(name = "users_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles;

	public void addAddress(Address address) {
		this.addressList.add(address);
	}

	public void removeAddress(Address address) {
		this.addressList.remove(address);
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.roles;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User) {
			return this.email.equals(((User) obj).email);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.email.hashCode();
	}
}