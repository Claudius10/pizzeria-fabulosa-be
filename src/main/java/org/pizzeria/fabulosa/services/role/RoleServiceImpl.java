package org.pizzeria.fabulosa.services.role;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.pizzeria.fabulosa.entity.role.Role;
import org.pizzeria.fabulosa.repos.role.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

	private RoleRepository roleRepository;

	@Override
	public void createRole(Role role) {
		this.roleRepository.save(role);
	}

	@Override
	public Optional<Role> findByName(String roleName) {
		return roleRepository.findByName(roleName);
	}
}