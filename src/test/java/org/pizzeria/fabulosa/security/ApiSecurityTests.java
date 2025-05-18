package org.pizzeria.fabulosa.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pizzeria.fabulosa.common.entity.role.Role;
import org.pizzeria.fabulosa.security.auth.JWTTokenManager;
import org.pizzeria.fabulosa.security.utils.SecurityCookies;
import org.pizzeria.fabulosa.web.dto.api.ResponseDTO;
import org.pizzeria.fabulosa.web.util.constant.ApiRoutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pizzeria.fabulosa.helpers.TestUtils.getResponse;
import static org.pizzeria.fabulosa.web.util.constant.SecurityConstants.ACCESS_TOKEN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureMockMvc
class ApiSecurityTests {

	@Container
	@ServiceConnection
	private final static MariaDBContainer db = new MariaDBContainer<>(DockerImageName.parse("mariadb:latest"));

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
		String validAccessToken = JWTTokenManager.generateAccessToken(
				"TokenTestRequestLogout@gmail.com",
				List.of(new Role("USER")),
				1L);

		// Act

		// post api call to log-out
		MockHttpServletResponse response = mockMvc.perform(post(ApiRoutes.AUTH_LOGOUT)
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, validAccessToken, 30, true, false)))
				.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(Objects.requireNonNull(response.getCookie(ACCESS_TOKEN)).getMaxAge()).isZero();
		assertThat(Objects.requireNonNull(response.getCookie(ACCESS_TOKEN)).getValue()).isEmpty();
	}

	@Test
	void givenApiCallToResource_whenValidAccessTokenAndRole_thenReturnOk() throws Exception {

		// Arrange

		// create access token
		String validAccessToken = JWTTokenManager.generateAccessToken(
				"TokenTestAccessProtectedResourceWithValidTokenAndRole@gmail.com",
				List.of(new Role("USER")),
				0L);

		// Act

		// post api call to check csrf protection
		MockHttpServletResponse response = mockMvc.perform(get("/api/tests")
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, validAccessToken, 30, true, false)))
				.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
	}

	@Test
	void givenApiCallToResource_whenValidAccessTokenAndInvalidRole_thenReturnUnauthorized() throws Exception {

		// Arrange

		// create access token
		String validAccessToken = JWTTokenManager.generateAccessToken(
				"TokenTestAccessProtectedResourceWithValidTokenAndRole@gmail.com",
				List.of(new Role("USER")),
				0L);

		// Act

		// post api call to check csrf protection
		MockHttpServletResponse response = mockMvc.perform(get("/api/tests/admin")
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, validAccessToken, 30, true, false)))


				.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
		ResponseDTO responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getApiError().isFatal()).isTrue();
	}

	@Test
	void givenApiCallToResource_whenNoToken_thenReturnUnauthorized() throws Exception {

		// Act

		// get api call to check security
		MockHttpServletResponse response = mockMvc.perform(get("/api/tests")).andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
		ResponseDTO responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getApiError().isFatal()).isTrue();
	}

	@Test
	void givenApiCallToResource_whenNoCookies_thenReturnUnauthorized() throws Exception {

		// Act

		// get api call to check security
		MockHttpServletResponse response =
				mockMvc.perform(get(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + "/" + "1")).andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}

	@Test
	void givenApiCallToResource_whenCookiesButNoAuth_thenReturnUnauthorized() throws Exception {

		// Act

		// get api call to check security
		MockHttpServletResponse response =
				mockMvc.perform(get(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + "/" + "1")
						.cookie(SecurityCookies.prepareCookie("randomCookie", "value", 1800, true, false))
				).andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}

	@Test
	void givenApiUnknownPath_thenReturnUnauthorized() throws Exception {

		// Act

		// get api call to check security
		MockHttpServletResponse response = mockMvc.perform(get("/")).andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}

	@Test
	void givenEvilRequest_thenReturnForbidden() throws Exception {

		// Act

		// get api call to check security
		MockHttpServletResponse response = mockMvc.perform(get("/;")).andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(404);
	}
}