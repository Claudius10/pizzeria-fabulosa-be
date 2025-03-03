package org.pizzeria.fabulosa.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pizzeria.fabulosa.services.user.UserService;
import org.pizzeria.fabulosa.utils.Constants;
import org.pizzeria.fabulosa.web.constants.ApiRoutes;
import org.pizzeria.fabulosa.web.constants.SecurityResponses;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.user.dto.RegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pizzeria.fabulosa.utils.TestUtils.getResponse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Sql(scripts = {"file:src/test/resources/db/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@DirtiesContext
class LoginTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeAll
	void setUp() {
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