package org.pizzeria.fabulosa.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pizzeria.fabulosa.configs.web.security.auth.JWTTokenManager;
import org.pizzeria.fabulosa.configs.web.security.utils.SecurityCookieUtils;
import org.pizzeria.fabulosa.entity.error.Error;
import org.pizzeria.fabulosa.entity.role.Role;
import org.pizzeria.fabulosa.repos.error.ErrorRepository;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pizzeria.fabulosa.utils.TestUtils.getResponse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@DirtiesContext
public class GlobalExceptionHandlerTests {

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
		String validAccessToken = tokenManager.getAccessToken(
				"errorTest@gmail.com",
				List.of(new Role("USER")),
				0L);

		// Act

		long errorCount = errorRepository.count();
		assertThat(errorCount).isZero();

		MockHttpServletResponse response = mockMvc.perform(post("/api/tests/error")
						.cookie(SecurityCookieUtils.prepareCookie(Constants.TOKEN_COOKIE_NAME, validAccessToken, 30, true, false)))
				.andReturn().getResponse();

		// Assert

		long errorCountAfter = errorRepository.count();
		assertThat(errorCountAfter).isGreaterThan(0);

		Optional<Error> error = errorRepository.findById(1L);
		assertThat(error).isPresent();
		assertThat(error.get().getMessage()).isEqualTo("TestError");
		assertThat(error.get().isLogged()).isEqualTo(true);
		assertThat(error.get().isFatal()).isEqualTo(true);

		Response responseObj = getResponse(response, objectMapper);
		assertThat(responseObj.getStatus().getCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
		assertThat(responseObj.getStatus().getDescription()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.name());
		assertThat(responseObj.getError().getCause()).isEqualTo(IllegalArgumentException.class.getSimpleName());
		assertThat(responseObj.getError().getMessage()).isEqualTo("TestError");
		assertThat(responseObj.getError().isLogged()).isEqualTo(true);
		assertThat(responseObj.getError().isFatal()).isEqualTo(true);
	}
}
