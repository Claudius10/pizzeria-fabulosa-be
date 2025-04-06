package org.pizzeria.fabulosa.web.controllers.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.configs.properties.SecurityProperties;
import org.pizzeria.fabulosa.configs.web.security.utils.SecurityCookieUtils;
import org.pizzeria.fabulosa.services.user.UserDetailsService;
import org.pizzeria.fabulosa.web.constants.ApiRoutes;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.api.Status;
import org.pizzeria.fabulosa.web.dto.user.dto.ContactNumberChangeDTO;
import org.pizzeria.fabulosa.web.dto.user.dto.EmailChangeDTO;
import org.pizzeria.fabulosa.web.dto.user.dto.NameChangeDTO;
import org.pizzeria.fabulosa.web.dto.user.dto.PasswordChangeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID)
@Validated
public class UserDetailsController {

	private final UserDetailsService userDetailsService;

	private final SecurityProperties securityProperties;

	@PutMapping(ApiRoutes.USER_NAME)
	public ResponseEntity<Response> updateName(@PathVariable Long userId, @Valid @RequestBody NameChangeDTO nameChangeDTO) {

		userDetailsService.updateUserName(nameChangeDTO.password(), userId, nameChangeDTO.name());

		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.OK.name())
						.code(HttpStatus.OK.value())
						.isError(false)
						.build())
				.build();

		return ResponseEntity.ok(response);
	}

	@PutMapping(ApiRoutes.USER_EMAIL)
	public ResponseEntity<Response> updateEmail(
			@PathVariable Long userId,
			@Valid @RequestBody EmailChangeDTO emailChangeDTO,
			HttpServletRequest request,
			HttpServletResponse response) {

		userDetailsService.updateUserEmail(emailChangeDTO.password(), userId, emailChangeDTO.email());

		Response responseObj = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.OK.name())
						.code(HttpStatus.OK.value())
						.isError(false)
						.build())
				.build();

		SecurityCookieUtils.eatAllCookies(request, response, securityProperties.getCookies().getDomain());

		return ResponseEntity.ok(responseObj);
	}

	@PutMapping(ApiRoutes.USER_NUMBER)
	public ResponseEntity<Response> updateContactNumber(
			@PathVariable Long userId,
			@Valid @RequestBody ContactNumberChangeDTO contactNumberChangeDTO) {

		userDetailsService.updateUserContactNumber(contactNumberChangeDTO.password(), userId, contactNumberChangeDTO.contactNumber());

		Response responseObj = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.OK.name())
						.code(HttpStatus.OK.value())
						.isError(false)
						.build())
				.build();

		return ResponseEntity.ok(responseObj);
	}

	@PutMapping(ApiRoutes.USER_PASSWORD)
	public ResponseEntity<Response> updatePassword(
			@PathVariable Long userId,
			@Valid @RequestBody PasswordChangeDTO passwordChangeDTO,
			HttpServletRequest request,
			HttpServletResponse response) {

		userDetailsService.updateUserPassword(passwordChangeDTO.currentPassword(), userId, passwordChangeDTO.newPassword());

		Response responseObj = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.OK.name())
						.code(HttpStatus.OK.value())
						.isError(false)
						.build())
				.build();

		SecurityCookieUtils.eatAllCookies(request, response, securityProperties.getCookies().getDomain());

		return ResponseEntity.ok(responseObj);
	}
}
