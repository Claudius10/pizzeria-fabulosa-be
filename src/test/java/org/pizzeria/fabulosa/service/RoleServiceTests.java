package org.pizzeria.fabulosa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pizzeria.fabulosa.entity.role.Role;
import org.pizzeria.fabulosa.services.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
public class RoleServiceTests {

	@Autowired
	private RoleService roleService;

	@Test
	void createRole() {

		// Arrange
		Role role = new Role();
		role.setName("BADASS");

		// Act
		roleService.createRole(role);

		// Assert
		Optional<Role> byName = roleService.findByName(role.getName());
		assertThat(byName.isPresent()).isTrue();
	}
}
