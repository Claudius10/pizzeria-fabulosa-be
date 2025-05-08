package org.pizzeria.fabulosa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pizzeria.fabulosa.entity.role.Role;
import org.pizzeria.fabulosa.services.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureMockMvc
public class RoleServiceTests {

	@Container
	@ServiceConnection
	private final static MariaDBContainer db = new MariaDBContainer<>(DockerImageName.parse("mariadb:latest"));

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
