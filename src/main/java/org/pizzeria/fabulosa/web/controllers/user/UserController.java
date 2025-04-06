package org.pizzeria.fabulosa.web.controllers.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.configs.properties.SecurityProperties;
import org.pizzeria.fabulosa.configs.web.security.utils.SecurityCookieUtils;
import org.pizzeria.fabulosa.entity.error.Error;
import org.pizzeria.fabulosa.services.user.UserService;
import org.pizzeria.fabulosa.web.aop.annotations.ValidateUserId;
import org.pizzeria.fabulosa.web.constants.ApiResponses;
import org.pizzeria.fabulosa.web.constants.ApiRoutes;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.api.Status;
import org.pizzeria.fabulosa.web.dto.user.dto.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE)
@Validated
public class UserController {

	private final UserService userService;

	private final SecurityProperties securityProperties;

	@ValidateUserId
	@GetMapping(ApiRoutes.USER_ID)
	public ResponseEntity<Response> findUserById(@PathVariable Long userId, HttpServletRequest request) {
		Optional<UserDTO> user = userService.findUserDTOById(userId);

		Response response = Response.builder()
				.status(Status.builder()
						.description(user.isPresent() ? HttpStatus.OK.name() : HttpStatus.NO_CONTENT.name())
						.code(user.isPresent() ? HttpStatus.OK.value() : HttpStatus.NO_CONTENT.value())
						.isError(false)
						.build())
				.payload(user.orElse(null))
				.build();

		return ResponseEntity.ok(response);
	}

	@ValidateUserId
	@DeleteMapping
	public ResponseEntity<Response> deleteUser(
			@RequestParam Long id,
			@RequestParam String password,
			HttpServletRequest request,
			HttpServletResponse response) {

		boolean isError = userService.deleteUserById(password, id);

		Response responseObj = Response.builder()
				.status(Status.builder()
						.description(isError ? HttpStatus.BAD_REQUEST.name() : HttpStatus.OK.name())
						.code(isError ? HttpStatus.BAD_REQUEST.value() : HttpStatus.OK.value())
						.isError(isError)
						.build())
				.build();

		if (isError) {
			responseObj.setError(Error.builder()
					.id(UUID.randomUUID().getMostSignificantBits())
					.cause(ApiResponses.DUMMY_ACCOUNT_ERROR)
					.origin(UserController.class.getSimpleName() + ".deleteUser")
					.path(request.getPathInfo())
					.logged(false)
					.fatal(false)
					.build());
		}

		if (!isError) {
			SecurityCookieUtils.eatAllCookies(request, response, securityProperties.getCookies().getDomain());
		}

		return ResponseEntity.ok(responseObj);
	}
}