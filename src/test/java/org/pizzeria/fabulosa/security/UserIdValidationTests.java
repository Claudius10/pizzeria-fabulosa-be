package org.pizzeria.fabulosa.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pizzeria.fabulosa.configs.web.security.auth.JWTTokenManager;
import org.pizzeria.fabulosa.configs.web.security.utils.SecurityCookieUtils;
import org.pizzeria.fabulosa.entity.role.Role;
import org.pizzeria.fabulosa.entity.user.User;
import org.pizzeria.fabulosa.repos.user.UserRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pizzeria.fabulosa.utils.TestUtils.getResponse;
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.ISOLATED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureMockMvc
@Sql(scripts = "file:src/test/resources/db/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, config = @SqlConfig(transactionMode = ISOLATED))
@Sql(scripts = "file:src/test/resources/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, config = @SqlConfig(transactionMode = ISOLATED))
class UserIdValidationTests {

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

	// test for ValidateUserIdentity aspect
	@Test
	void givenAccessToProtectedResource_whenNonMatchingUserIdCookieAndJwtUserIdClaim_thenReturnUnauthorized() throws Exception {

		// Arrange

		// create user test subject

		long testUserId = createUser(new RegisterDTO(
				"UserIdValidation",
				"UserIdValidationTestNonMatchingCookieUserIdAndJwtuserId@gmail.com",
				"UserIdValidationTestNonMatchingCookieUserIdAndJwtuserId@gmail.com",
				123456789,
				"Password1",
				"Password1"));

		// create JWT token
		String accessToken = JWTTokenManager.generateAccessToken(
				"UserIdValidationTestNonMatchingCookieUserIdAndJwtuserId@gmail.com",
				List.of(new Role("USER")),
				testUserId);

		Long nonMatchingUserId = 9999L;

		// Act

		MockHttpServletResponse response = mockMvc.perform(get(
						ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID, nonMatchingUserId)
						.cookie(SecurityCookieUtils.prepareCookie(Constants.AUTH_TOKEN, accessToken, 1800, true, false)))
				.andReturn()
				.getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
		assertThat(responseObj.getError().getCause()).isEqualTo(SecurityResponses.USER_ID_NO_MATCH);
	}

	@Test
	void givenAccessToProtectedResource_whenAllOk_thenReturnOk() throws Exception {

		// Arrange

		// create user test subject

		long testUserId = createUser(new RegisterDTO(
				"UserIdValidation",
				"UserIdValidationTestMatchingUserIdCookieAndJwtUserIdClaim@gmail.com",
				"UserIdValidationTestMatchingUserIdCookieAndJwtUserIdClaim@gmail.com",
				123456789,
				"Password1",
				"Password1"));

		// create JWT token

		String accessToken = JWTTokenManager.generateAccessToken(
				"UserIdValidationTestMatchingUserIdCookieAndJwtUserIdClaim@gmail.com",
				List.of(new Role("USER")),
				testUserId);

		// Act

		MockHttpServletResponse response = mockMvc.perform(get(
						ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID, testUserId)
						.cookie(SecurityCookieUtils.prepareCookie(Constants.AUTH_TOKEN, accessToken, 1800, true, false)))
				.andReturn()
				.getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.OK.value());
	}

	Long createUser(RegisterDTO registerDTO) throws Exception {
		mockMvc.perform(post(
				ApiRoutes.BASE
						+ ApiRoutes.V1
						+ ApiRoutes.ANON_BASE
						+ ApiRoutes.ANON_REGISTER)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(registerDTO)
				));

		Optional<User> user = userRepository.findUserByEmailWithRoles(registerDTO.email());
		assertThat(user.isPresent()).isTrue();
		return user.get().getId();
	}
}