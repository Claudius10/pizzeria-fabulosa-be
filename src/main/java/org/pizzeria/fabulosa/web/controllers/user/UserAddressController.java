package org.pizzeria.fabulosa.web.controllers.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.common.entity.address.Address;
import org.pizzeria.fabulosa.security.utils.UserSecurity;
import org.pizzeria.fabulosa.web.controllers.user.swagger.UserAddressControllerSwagger;
import org.pizzeria.fabulosa.web.dto.order.AddressDTO;
import org.pizzeria.fabulosa.web.service.user.UserAddressService;
import org.pizzeria.fabulosa.web.util.constant.ApiResponses;
import org.pizzeria.fabulosa.web.util.constant.ApiRoutes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

import static org.pizzeria.fabulosa.web.util.ResponseUtils.error;

@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE + ApiRoutes.USER_ID + ApiRoutes.USER_ADDRESS)
public class UserAddressController implements UserAddressControllerSwagger {

	private final UserAddressService userAddressService;

	@GetMapping
	public ResponseEntity<?> findUserAddressListById(@PathVariable Long userId, HttpServletRequest request) {
		if (!UserSecurity.valid(userId)) {
			return UserSecurity.deny(request);
		}

		Set<Address> userAddressList = userAddressService.findUserAddressListById(userId);

		if (userAddressList.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		Set<AddressDTO> addresses = userAddressList.stream().map(address -> new AddressDTO(
						address.getId(),
						address.getStreet(),
						address.getNumber(),
						address.getDetails()))
				.collect(Collectors.toSet());

		return ResponseEntity.ok(addresses);
	}

	@PostMapping
	public ResponseEntity<?> createUserAddress(@RequestBody @Valid AddressDTO address, @PathVariable Long userId, HttpServletRequest request) {
		if (!UserSecurity.valid(userId)) {
			return UserSecurity.deny(request);
		}

		boolean ok = userAddressService.addUserAddress(userId, Address.fromDTOBuilder().build(address));

		if (!ok) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error(this.getClass().getSimpleName(), ApiResponses.ADDRESS_MAX_SIZE, request.getPathInfo()));
		}

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping(ApiRoutes.USER_ADDRESS_ID)
	public ResponseEntity<?> deleteUserAddress(@PathVariable Long addressId, @PathVariable Long userId, HttpServletRequest request) {
		if (!UserSecurity.valid(userId)) {
			return UserSecurity.deny(request);
		}

		boolean result = userAddressService.removeUserAddress(userId, addressId);

		if (!result) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error(this.getClass().getSimpleName(), ApiResponses.ADDRESS_NOT_FOUND, request.getPathInfo()));
		}

		return ResponseEntity.ok().build();
	}
}
