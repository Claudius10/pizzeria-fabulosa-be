package org.pizzeria.fabulosa.web.controllers.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.common.entity.address.Address;
import org.pizzeria.fabulosa.security.utils.UserSecurity;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.service.user.UserAddressService;
import org.pizzeria.fabulosa.web.util.constant.ApiResponses;
import org.pizzeria.fabulosa.web.util.constant.ApiRoutes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static org.pizzeria.fabulosa.web.util.ResponseUtils.error;

@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS)
@Validated
public class UserAddressController {

	private final UserAddressService userAddressService;

	@GetMapping
	public ResponseEntity<Response> findUserAddressListById(@PathVariable Long userId, HttpServletRequest request) {
		if (!UserSecurity.valid(userId)) {
			return UserSecurity.deny(request);
		}

		Set<Address> userAddressList = userAddressService.findUserAddressListById(userId);

		if (userAddressList.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		return ResponseEntity.ok(Response.builder().payload(userAddressList).build());
	}

	@PostMapping
	public ResponseEntity<Response> createUserAddress(@RequestBody @Valid Address address, @PathVariable Long userId, HttpServletRequest request) {
		if (!UserSecurity.valid(userId)) {
			return UserSecurity.deny(request);
		}

		boolean ok = userAddressService.addUserAddress(userId, address);

		if (!ok) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error(this.getClass().getSimpleName(), ApiResponses.ADDRESS_MAX_SIZE, request.getPathInfo()));
		}

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping(ApiRoutes.USER_ADDRESS_ID)
	public ResponseEntity<Response> deleteUserAddress(@PathVariable Long addressId, @PathVariable Long userId, HttpServletRequest request) {
		if (!UserSecurity.valid(userId)) {
			return UserSecurity.deny(request);
		}

		boolean result = userAddressService.removeUserAddress(userId, addressId);

		if (!result) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		return ResponseEntity.ok().build();
	}
}
