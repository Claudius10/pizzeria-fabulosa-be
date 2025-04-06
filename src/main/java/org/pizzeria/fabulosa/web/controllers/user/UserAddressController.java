package org.pizzeria.fabulosa.web.controllers.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.entity.address.Address;
import org.pizzeria.fabulosa.entity.error.Error;
import org.pizzeria.fabulosa.services.user.UserAddressService;
import org.pizzeria.fabulosa.web.aop.annotations.ValidateUserId;
import org.pizzeria.fabulosa.web.constants.ApiResponses;
import org.pizzeria.fabulosa.web.constants.ApiRoutes;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.dto.api.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS)
@Validated
public class UserAddressController {

	private final UserAddressService userAddressService;

	@ValidateUserId
	@GetMapping
	public ResponseEntity<Response> findUserAddressListById(@PathVariable Long userId, HttpServletRequest request) {
		Set<Address> userAddressList = userAddressService.findUserAddressListById(userId);

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
	@PostMapping
	public ResponseEntity<Response> createUserAddress(
			@RequestBody @Valid Address address, @PathVariable Long userId, HttpServletRequest request) {
		boolean ok = userAddressService.addUserAddress(userId, address);

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

		return ResponseEntity.ok(response);
	}

	@ValidateUserId
	@DeleteMapping(ApiRoutes.USER_ADDRESS_ID)
	public ResponseEntity<Response> deleteUserAddress(@PathVariable Long addressId, @PathVariable Long userId, HttpServletRequest request) {
		boolean result = userAddressService.removeUserAddress(userId, addressId);

		Response response = Response.builder()
				.status(Status.builder()
						.description(result ? HttpStatus.OK.name() : HttpStatus.NO_CONTENT.name())
						.code(result ? HttpStatus.OK.value() : HttpStatus.NO_CONTENT.value())
						.isError(false)
						.build())
				.build();

		return ResponseEntity.ok(response);
	}
}
