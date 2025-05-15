package org.pizzeria.fabulosa.common.service.role;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.common.dao.role.RoleRepository;
import org.pizzeria.fabulosa.common.entity.role.Role;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

	private final RoleRepository roleRepository;

	@Override
	public void createRole(Role role) {
		this.roleRepository.save(role);
	}

	@Override
	public Optional<Role> findByName(String roleName) {
		return roleRepository.findByName(roleName);
	}
}