package org.pizzeria.fabulosa.web.controller.locked;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pizzeria.fabulosa.common.dao.user.UserRepository;
import org.pizzeria.fabulosa.common.entity.role.Role;
import org.pizzeria.fabulosa.common.entity.user.User;
import org.pizzeria.fabulosa.security.auth.JWTTokenManager;
import org.pizzeria.fabulosa.security.utils.SecurityCookies;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.user.RegisterDTO;
import org.pizzeria.fabulosa.web.util.constant.ApiResponses;
import org.pizzeria.fabulosa.web.util.constant.ApiRoutes;
import org.pizzeria.fabulosa.web.util.constant.SecurityResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.pizzeria.fabulosa.common.util.constant.AppConstants.DUMMY_ACCOUNT_EMAIL;
import static org.pizzeria.fabulosa.helpers.TestUtils.getResponse;
import static org.pizzeria.fabulosa.web.util.constant.SecurityConstants.ACCESS_TOKEN;
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.ISOLATED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureMockMvc
@Sql(scripts = "file:src/test/resources/db/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, config = @SqlConfig(transactionMode = ISOLATED))
@Sql(scripts = "file:src/test/resources/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, config = @SqlConfig(transactionMode = ISOLATED))
class UserControllerTests {

	@Container
	@ServiceConnection
	private final static MariaDBContainer db = new MariaDBContainer<>(DockerImageName.parse("mariadb:latest"));

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private JWTTokenManager JWTTokenManager;

	@Autowired
	private UserRepository userRepository;

	@Test
	void givenDeleteUserApiCall_thenDeleteUserAndDeleteAuthTokenCookie() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser("Tester3@gmail.com");

		Optional<User> user = userRepository.findUserByEmailWithRoles("Tester3@gmail.com");
		assertThat(user).isPresent();

		// create JWT token
		String accessToken = JWTTokenManager.generateAccessToken("Tester3@gmail.com", List.of(new Role("USER")), userId);

		// create dto object
		String password = "Password1";

		// Act

		// put api call to delete the user
		MockHttpServletResponse response = mockMvc.perform(delete(
						ApiRoutes.BASE +
								ApiRoutes.V1 +
								ApiRoutes.USER_BASE + "?id={userId}&password={password}", userId, password)
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 1800, true, false)))
				.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		Optional<User> userAfter = userRepository.findUserByEmailWithRoles("Tester3@gmail.com");
		assertThat(userAfter).isEmpty();

		Optional<Cookie> authCookie = Arrays.stream(response.getCookies()).filter(cookie -> cookie.getName().equals(ACCESS_TOKEN)).findFirst();
		if (authCookie.isPresent()) {
			assertThat(authCookie.get().getValue()).isEqualTo("");
			assertThat(authCookie.get().getMaxAge()).isEqualTo(0);
		} else {
			fail(ACCESS_TOKEN + " cookie not found");
		}
	}

	@Test
	void givenDeleteDummyUserApiCall_thenReturnInternalServerErrorWithMessage() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser(DUMMY_ACCOUNT_EMAIL);

		// create JWT token
		String accessToken = JWTTokenManager.generateAccessToken(DUMMY_ACCOUNT_EMAIL, List.of(new Role("USER")), userId);

		// create dto object
		String password = "Password1";

		// Act

		// put api call to delete the user
		MockHttpServletResponse response = mockMvc.perform(delete(
						ApiRoutes.BASE +
								ApiRoutes.V1 +
								ApiRoutes.USER_BASE + "?id={userId}&password={password}", userId, password)
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 1800, true, false)))
				.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getApiError().getCause()).isEqualTo(ApiResponses.DUMMY_ACCOUNT_ERROR);
	}

	@Test
	void givenDeleteUserApiCall_whenInvalidCurrentPassword_thenThrowBadCredentialsException() throws Exception {
		// Arrange

		// post api call to register new user in database
		Long userId = createUser("Tester3@gmail.com");

		// create JWT token
		String accessToken = JWTTokenManager.generateAccessToken("Tester3@gmail.com", List.of(new Role("USER")), userId);

		// password
		String password = "WrongPassword";

		// Act

		// delete api call to delete user
		MockHttpServletResponse response = mockMvc.perform(delete(
						ApiRoutes.BASE +
								ApiRoutes.V1 +
								ApiRoutes.USER_BASE + "?id={userId}&password={password}", userId, password)
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, accessToken, 1800, true, false)))
				.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getApiError().getCause()).isEqualTo(SecurityResponses.BAD_CREDENTIALS);
	}

	// ------------------------- HELPERS -------------------------

	Long createUser(String email) throws Exception {

		mockMvc.perform(post(
				ApiRoutes.BASE
						+ ApiRoutes.V1
						+ ApiRoutes.ANON_BASE
						+ ApiRoutes.ANON_REGISTER)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new RegisterDTO(
						"Tester",
						email,
						email,
						123456789,
						"Password1",
						"Password1")
				)));

		Optional<User> user = userRepository.findUserByEmailWithRoles(email);
		assertThat(user.isPresent()).isTrue();
		return user.get().getId();
	}
}