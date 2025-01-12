package org.pizzeria.fabulosa.entity.user;

import jakarta.persistence.*;
import org.pizzeria.fabulosa.entity.address.Address;
import org.pizzeria.fabulosa.entity.role.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "User")
@Table(name = "user",
		uniqueConstraints = @UniqueConstraint(name = "USER_EMAIL", columnNames = "email")
)
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
	@SequenceGenerator(name = "user_generator", sequenceName = "user_seq", allocationSize = 1)
	private Long id;

	@Column
	private String name;

	@Column
	private String email;

	@Column
	private Integer contactNumber;

	@Column
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

	public User() {
		// The JPA specification requires all Entity classes to have a default no-arg constructor.
	}

	private User(Builder builder) {
		this.name = builder.name;
		this.email = builder.email;
		this.contactNumber = builder.contactNumber;
		this.password = builder.password;
		this.roles = builder.roles;
	}

	public void addAddress(Address address) {
		this.addressList.add(address);
	}

	public void removeAddress(Address address) {
		this.addressList.remove(address);
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(Integer contactNumber) {
		this.contactNumber = contactNumber;
	}

	public Set<Address> getAddressList() {
		return addressList;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.roles;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
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

	public static class Builder {

		private final Set<Role> roles;
		private String email, password, name;
		private Integer contactNumber;

		public Builder() {
			this.roles = new HashSet<>();
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withEmail(String email) {
			this.email = email;
			return this;
		}

		public Builder withContactNumber(Integer contactNumber) {
			this.contactNumber = contactNumber;
			return this;
		}

		public Builder withPassword(String password) {
			this.password = password;
			return this;
		}

		public Builder withRoles(Role... roles) {
			this.roles.addAll(Arrays.asList(roles));
			return this;
		}

		public User build() {
			return new User(this);
		}
	}
}