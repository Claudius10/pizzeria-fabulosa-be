package org.pizzeria.fabulosa.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pizzeria.fabulosa.configs.web.security.auth.JWTTokenManager;
import org.pizzeria.fabulosa.configs.web.security.utils.SecurityCookieUtils;
import org.pizzeria.fabulosa.entity.role.Role;
import org.pizzeria.fabulosa.entity.user.User;
import org.pizzeria.fabulosa.services.user.UserService;
import org.pizzeria.fabulosa.utils.Constants;
import org.pizzeria.fabulosa.utils.TestUtils;
import org.pizzeria.fabulosa.web.constants.ApiRoutes;
import org.pizzeria.fabulosa.web.constants.SecurityResponses;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.user.dto.RegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pizzeria.fabulosa.utils.TestUtils.getResponse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Sql(scripts = {"file:src/test/resources/db/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@DirtiesContext
class UserIdValidationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private JWTTokenManager JWTTokenManager;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private UserService userService;

	@BeforeAll
	@AfterAll
	void cleanUp() {
		JdbcTestUtils.deleteFromTables(jdbcTemplate, "users_roles", "users_addresses", "user");
	}

	// test for ValidateUserIdentity aspect
	@Test
	void givenAccessToProtectedResource_whenNonMatchingUserIdCookieAndJwtUserIdClaim_thenReturnUnauthorized() throws Exception {

		// Arrange

		// create user test subject

		User user = TestUtils.createUser(userService, new RegisterDTO(
				"UserIdValidation",
				"UserIdValidationTestNonMatchingCookieUserIdAndJwtuserId@gmail.com",
				"UserIdValidationTestNonMatchingCookieUserIdAndJwtuserId@gmail.com",
				123456789,
				"Password1",
				"Password1"));

		// create JWT token
		String accessToken = JWTTokenManager.getAccessToken(
				"UserIdValidationTestNonMatchingCookieUserIdAndJwtuserId@gmail.com",
				List.of(new Role("USER")),
				user.getId());

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

		User user = TestUtils.createUser(userService, new RegisterDTO(
				"UserIdValidation",
				"UserIdValidationTestMatchingUserIdCookieAndJwtUserIdClaim@gmail.com",
				"UserIdValidationTestMatchingUserIdCookieAndJwtUserIdClaim@gmail.com",
				123456789,
				"Password1",
				"Password1"));

		// create JWT token

		String accessToken = JWTTokenManager.getAccessToken(
				"UserIdValidationTestMatchingUserIdCookieAndJwtUserIdClaim@gmail.com",
				List.of(new Role("USER")),
				user.getId());

		// Act

		MockHttpServletResponse response = mockMvc.perform(get(
						ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID, user.getId())
						.cookie(SecurityCookieUtils.prepareCookie(Constants.AUTH_TOKEN, accessToken, 1800, true, false)))
				.andReturn()
				.getResponse();

		// Assert

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.OK.value());
	}
}