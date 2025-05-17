package org.pizzeria.fabulosa.web.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pizzeria.fabulosa.common.dao.error.ErrorRepository;
import org.pizzeria.fabulosa.common.entity.error.Error;
import org.pizzeria.fabulosa.common.entity.role.Role;
import org.pizzeria.fabulosa.security.auth.JWTTokenManager;
import org.pizzeria.fabulosa.security.utils.SecurityCookies;
import org.pizzeria.fabulosa.web.dto.api.Response;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pizzeria.fabulosa.helpers.TestUtils.getResponse;
import static org.pizzeria.fabulosa.web.util.constant.SecurityConstants.ACCESS_TOKEN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureMockMvc
public class GlobalExceptionHandlerTests {

	@Container
	@ServiceConnection
	private final static MariaDBContainer db = new MariaDBContainer<>(DockerImageName.parse("mariadb:latest"));

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JWTTokenManager tokenManager;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ErrorRepository errorRepository;

	@Test
	public void givenApiCall_whenUnhandledException_thenReturnInternalServerErrorAndLogError() throws Exception {
		// Arrange

		// create access token
		String validAccessToken = tokenManager.generateAccessToken(
				"errorTest@gmail.com",
				List.of(new Role("USER")),
				0L);

		// Act

		long errorCount = errorRepository.count();
		assertThat(errorCount).isZero();

		MockHttpServletResponse response = mockMvc.perform(post("/api/tests/error")
						.cookie(SecurityCookies.prepareCookie(ACCESS_TOKEN, validAccessToken, 30, true, false)))
				.andReturn().getResponse();

		// Assert

		assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
		long errorCountAfter = errorRepository.count();
		assertThat(errorCountAfter).isGreaterThan(0);

		Optional<Error> error = errorRepository.findById(1L);
		assertThat(error).isPresent();
		assertThat(error.get().getMessage()).isEqualTo("TestError");
		assertThat(error.get().isLogged()).isEqualTo(true);
		assertThat(error.get().isFatal()).isEqualTo(true);

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getError().getCause()).isEqualTo(IllegalArgumentException.class.getSimpleName());
		assertThat(responseObj.getError().getMessage()).isEqualTo("TestError");
		assertThat(responseObj.getError().isLogged()).isEqualTo(true);
		assertThat(responseObj.getError().isFatal()).isEqualTo(true);
	}
}
