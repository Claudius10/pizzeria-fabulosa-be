package org.pizzeria.fabulosa.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pizzeria.fabulosa.configs.web.security.auth.JWTTokenManager;
import org.pizzeria.fabulosa.configs.web.security.utils.SecurityCookieUtils;
import org.pizzeria.fabulosa.entity.role.Role;
import org.pizzeria.fabulosa.utils.Constants;
import org.pizzeria.fabulosa.web.constants.ApiRoutes;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pizzeria.fabulosa.utils.TestUtils.getResponse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
class ApiSecurityTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JWTTokenManager JWTTokenManager;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void givenLogoutApiCall_whenCredentialsArePresent_thenEraseCredentials() throws Exception {
		// Arrange

		// create access token
		String validAccessToken = JWTTokenManager.getAccessToken(
				"TokenTestRequestLogout@gmail.com",
				List.of(new Role("USER")),
				1L);

		// Act

		// post api call to log-out
		MockHttpServletResponse response = mockMvc.perform(post(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.AUTH_BASE + ApiRoutes.AUTH_LOGOUT)
						.cookie(SecurityCookieUtils.prepareCookie(Constants.AUTH_TOKEN, validAccessToken, 30, true, false)))
				.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(Objects.requireNonNull(response.getCookie(Constants.AUTH_TOKEN)).getMaxAge()).isZero();
		assertThat(Objects.requireNonNull(response.getCookie(Constants.AUTH_TOKEN)).getValue()).isEmpty();
	}

	@Test
	void givenApiCallToResource_whenValidAccessTokenAndRole_thenReturnOk() throws Exception {
		// Arrange

		// create access token
		String validAccessToken = JWTTokenManager.getAccessToken(
				"TokenTestAccessProtectedResourceWithValidTokenAndRole@gmail.com",
				List.of(new Role("USER")),
				0L);

		// Act

		// post api call to check csrf protection
		MockHttpServletResponse response = mockMvc.perform(get("/api/tests")
						.cookie(SecurityCookieUtils.prepareCookie(Constants.AUTH_TOKEN, validAccessToken, 30, true, false)))
				.andReturn().getResponse();

		Response responseObj = getResponse(response, objectMapper);

		// Assert

		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(responseObj.getStatus().isError()).isFalse();
	}

	@Test
	void givenApiCallToResource_whenValidAccessTokenAndInvalidRole_thenReturnUnauthorized() throws Exception {
		// Arrange

		// create access token
		String validAccessToken = JWTTokenManager.getAccessToken(
				"TokenTestAccessProtectedResourceWithValidTokenAndRole@gmail.com",
				List.of(new Role("USER")),
				0L);

		// Act

		// post api call to check csrf protection
		MockHttpServletResponse response = mockMvc.perform(get("/api/tests/admin")
						.cookie(SecurityCookieUtils.prepareCookie(Constants.AUTH_TOKEN, validAccessToken, 30, true, false)))


				.andReturn().getResponse();

		Response responseObj = getResponse(response, objectMapper);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
		assertThat(responseObj.getStatus().isError()).isTrue();
		assertThat(responseObj.getError().isFatal()).isTrue();
	}

	@Test
	void givenApiCallToResource_whenNoToken_thenReturnUnauthorized() throws Exception {
		// Act

		// get api call to check security
		MockHttpServletResponse response = mockMvc.perform(get("/api/tests")).andReturn().getResponse();
		Response responseObj = getResponse(response, objectMapper);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
		assertThat(responseObj.getStatus().isError()).isTrue();
		assertThat(responseObj.getError().isFatal()).isTrue();
	}

	@Test
	void givenApiCallToResource_whenNoCookies_thenReturnUnauthorized() throws Exception {
		// Act

		// get api call to check security
		MockHttpServletResponse response =
				mockMvc.perform(get(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + "/" + "1")).andReturn().getResponse();
		Response responseObj = getResponse(response, objectMapper);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
		assertThat(responseObj.getStatus().isError()).isTrue();
	}

	@Test
	void givenApiCallToResource_whenCookiesButNoAuth_thenReturnUnauthorized() throws Exception {
		// Act

		// get api call to check security
		MockHttpServletResponse response =
				mockMvc.perform(get(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + "/" + "1")
						.cookie(SecurityCookieUtils.prepareCookie("randomCookie", "value", 1800, true, false))
				).andReturn().getResponse();
		Response responseObj = getResponse(response, objectMapper);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
		assertThat(responseObj.getStatus().isError()).isTrue();
	}

	@Test
	void givenApiUnknownPath_thenReturnUnauthorized() throws Exception {
		// Act

		// get api call to check security
		MockHttpServletResponse response = mockMvc.perform(get("/")).andReturn().getResponse();
		Response responseObj = getResponse(response, objectMapper);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
		assertThat(responseObj.getStatus().isError()).isTrue();
	}

	@Test
	void givenEvilRequest_thenReturnForbidden() throws Exception {
		// Act

		// get api call to check security
		MockHttpServletResponse response = mockMvc.perform(get("/;")).andReturn().getResponse();
		assertThat(response.getStatus()).isEqualTo(404);
	}
}