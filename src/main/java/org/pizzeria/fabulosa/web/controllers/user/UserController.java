package org.pizzeria.fabulosa.web.controllers.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.pizzeria.fabulosa.configs.web.security.utils.SecurityCookieUtils;
import org.pizzeria.fabulosa.entity.address.Address;
import org.pizzeria.fabulosa.entity.error.Error;
import org.pizzeria.fabulosa.services.user.UserService;
import org.pizzeria.fabulosa.web.aop.annotations.ValidateUserId;
import org.pizzeria.fabulosa.web.constants.ApiResponses;
import org.pizzeria.fabulosa.web.constants.ApiRoutes;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.api.Status;
import org.pizzeria.fabulosa.web.dto.user.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE)
@Validated
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

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
	@GetMapping(ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS)
	public ResponseEntity<Response> findUserAddressListById(@PathVariable Long userId, HttpServletRequest request) {
		Set<Address> userAddressList = userService.findUserAddressListById(userId);

		Response response = Response.builder()
				.status(Status.builder()
						.description(!userAddressList.isEmpty() ? HttpStatus.OK.name() : HttpStatus.NO_CONTENT.name())
						.code(!userAddressList.isEmpty() ? HttpStatus.OK.value() : HttpStatus.NO_CONTENT.value())
						.isError(false)
						.build())
				.payload(userAddressList.isEmpty() ? null : userAddressList)
				.build();

		return ResponseEntity.ok(response);
	}

	@ValidateUserId
	@PostMapping(ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS)
	public ResponseEntity<Response> createUserAddress(@RequestBody @Valid Address address, @PathVariable Long userId, HttpServletRequest request) {
		boolean ok = userService.addUserAddress(userId, address);

		Response response = Response.builder()
				.status(Status.builder()
						.description(ok ? HttpStatus.CREATED.name() : HttpStatus.BAD_REQUEST.name())
						.code(ok ? HttpStatus.CREATED.value() : HttpStatus.BAD_REQUEST.value())
						.isError(!ok)
						.build())
				.build();

		if (!ok) {
			response.setError(Error.builder()
					.id(UUID.randomUUID().getMostSignificantBits())
					.cause(ApiResponses.ADDRESS_MAX_SIZE)
					.origin(UserController.class.getSimpleName() + ".createUserAddress")
					.path(request.getPathInfo())
					.logged(false)
					.fatal(false)
					.build());
		}

		return ResponseEntity.status(ok ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(response);
	}

	@ValidateUserId
	@DeleteMapping(ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS + ApiRoutes.USER_ADDRESS_ID)
	public ResponseEntity<Response> deleteUserAddress(@PathVariable Long addressId, @PathVariable Long userId, HttpServletRequest request) {
		boolean result = userService.removeUserAddress(userId, addressId);

		Response response = Response.builder()
				.status(Status.builder()
						.description(result ? HttpStatus.OK.name() : HttpStatus.NO_CONTENT.name())
						.code(result ? HttpStatus.OK.value() : HttpStatus.NO_CONTENT.value())
						.isError(false)
						.build())
				.build();

		return ResponseEntity.ok(response);
	}

	@PutMapping(ApiRoutes.USER_ID + ApiRoutes.USER_NAME)
	public ResponseEntity<Response> updateName(@PathVariable Long userId, @Valid @RequestBody NameChangeDTO nameChangeDTO) {

		userService.updateUserName(nameChangeDTO.password(), userId, nameChangeDTO.name());

		Response response = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.OK.name())
						.code(HttpStatus.OK.value())
						.isError(false)
						.build())
				.build();

		return ResponseEntity.ok(response);
	}

	@PutMapping(ApiRoutes.USER_ID + ApiRoutes.USER_EMAIL)
	public ResponseEntity<Response> updateEmail(
			@PathVariable Long userId,
			@Valid @RequestBody EmailChangeDTO emailChangeDTO,
			HttpServletRequest request,
			HttpServletResponse response) {

		userService.updateUserEmail(emailChangeDTO.password(), userId, emailChangeDTO.email());

		Response responseObj = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.OK.name())
						.code(HttpStatus.OK.value())
						.isError(false)
						.build())
				.build();

		SecurityCookieUtils.eatAllCookies(request, response);

		return ResponseEntity.ok(responseObj);
	}

	@PutMapping(ApiRoutes.USER_ID + ApiRoutes.USER_NUMBER)
	public ResponseEntity<Response> updateContactNumber(
			@PathVariable Long userId,
			@Valid @RequestBody ContactNumberChangeDTO contactNumberChangeDTO) {

		userService.updateUserContactNumber(contactNumberChangeDTO.password(), userId, contactNumberChangeDTO.contactNumber());

		Response responseObj = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.OK.name())
						.code(HttpStatus.OK.value())
						.isError(false)
						.build())
				.build();

		return ResponseEntity.ok(responseObj);
	}

	@PutMapping(ApiRoutes.USER_ID + ApiRoutes.USER_PASSWORD)
	public ResponseEntity<Response> updatePassword(
			@PathVariable Long userId,
			@Valid @RequestBody PasswordChangeDTO passwordChangeDTO,
			HttpServletRequest request,
			HttpServletResponse response) {

		userService.updateUserPassword(passwordChangeDTO.currentPassword(), userId, passwordChangeDTO.newPassword());

		Response responseObj = Response.builder()
				.status(Status.builder()
						.description(HttpStatus.OK.name())
						.code(HttpStatus.OK.value())
						.isError(false)
						.build())
				.build();

		SecurityCookieUtils.eatAllCookies(request, response);

		return ResponseEntity.ok(responseObj);
	}

	@DeleteMapping()
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
			SecurityCookieUtils.eatAllCookies(request, response);
		}

		return ResponseEntity.ok(responseObj);
	}
}