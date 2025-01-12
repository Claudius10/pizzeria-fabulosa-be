package org.pizzeria.fabulosa.services.role;

import org.pizzeria.fabulosa.entity.role.Role;

import java.util.Optional;

public interface RoleService {

	void createRole(Role role);

	Optional<Role> findByName(String roleName);
}