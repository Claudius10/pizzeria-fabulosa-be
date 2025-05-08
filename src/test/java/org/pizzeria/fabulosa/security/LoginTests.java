package org.pizzeria.fabulosa.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pizzeria.fabulosa.entity.role.Role;
import org.pizzeria.fabulosa.services.role.RoleService;
import org.pizzeria.fabulosa.services.user.UserService;
import org.pizzeria.fabulosa.utils.Constants;
import org.pizzeria.fabulosa.web.constants.ApiRoutes;
import org.pizzeria.fabulosa.web.constants.SecurityResponses;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.user.dto.RegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pizzeria.fabulosa.utils.TestUtils.getResponse;
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.ISOLATED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext
@AutoConfigureMockMvc
@Sql(scripts = "file:src/test/resources/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, config = @SqlConfig(transactionMode = ISOLATED))
class LoginTests {

	@Container
	@ServiceConnection
	private final static MariaDBContainer db = new MariaDBContainer<>(DockerImageName.parse("mariadb:latest"));

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeAll
	void setUp() {
		roleService.createRole(new Role("USER"));

		userService.createUser(new RegisterDTO(
				"tester",
				"test@gmail.com",
				"test@gmail.com",
				123456789,
				"password",
				"password"));
	}

	@Test
	void givenLoginApiCall_whenValidCredentials_thenReturnOk() throws Exception {
		// Act

		// post api call to log in
		MockHttpServletResponse response = mockMvc.perform(post(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.AUTH_BASE
								+ ApiRoutes.AUTH_LOGIN
								+ "?username=test@gmail.com&password=password"))
				.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getCookies()).isNotEmpty();
		assertThat(response.getCookie(Constants.AUTH_TOKEN)).isNotNull();
		assertThat(response.getCookie(Constants.ID_TOKEN)).isNotNull();
	}

	@Test
	void givenLoginApiCall_whenInvalidCredentials_thenReturnUnauthorized() throws Exception {
		// Act

		// post api call to log in
		MockHttpServletResponse response = mockMvc.perform(post(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.AUTH_BASE
								+ ApiRoutes.AUTH_LOGIN +
								"?username=void@email.com&password=randomPassword"))
				.andReturn().getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
		assertThat(responseObj.getStatus().isError()).isTrue();
		assertThat(responseObj.getError().getCause()).isEqualTo(SecurityResponses.BAD_CREDENTIALS);
		assertThat(response.getCookies()).isEmpty();
	}

	@Test
	void givenLoginApiCall_whenInvalidPassword_thenReturnUnauthorized() throws Exception {
		// Act

		// post api call to log in
		MockHttpServletResponse response = mockMvc.perform(post(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.AUTH_BASE
								+ ApiRoutes.AUTH_LOGIN +
								"?username=test@gmail.com&password=wrong_password"))
				.andReturn().getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
		assertThat(responseObj.getStatus().isError()).isTrue();
		assertThat(responseObj.getError().getCause()).isEqualTo(SecurityResponses.BAD_CREDENTIALS);
		assertThat(response.getCookies()).isEmpty();
	}

	@Test
	void givenLoginApiCall_whenInvalidUsername_thenReturnUnauthorized() throws Exception {
		// Act

		// post api call to log in
		MockHttpServletResponse response = mockMvc.perform(post(
						ApiRoutes.BASE
								+ ApiRoutes.V1
								+ ApiRoutes.AUTH_BASE
								+ ApiRoutes.AUTH_LOGIN +
								"?username=nottest@gmail.com&password=password"))
				.andReturn().getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
		assertThat(responseObj.getStatus().isError()).isTrue();
		assertThat(responseObj.getError().getCause()).isEqualTo(SecurityResponses.BAD_CREDENTIALS);
		assertThat(response.getCookies()).isEmpty();
	}
}