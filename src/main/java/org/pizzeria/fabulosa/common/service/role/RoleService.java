package org.pizzeria.fabulosa.common.service.role;

import org.pizzeria.fabulosa.common.entity.role.Role;

import java.util.Optional;

public interface RoleService {

	void createRole(Role role);

	Optional<Role> findByName(String roleName);
}