package org.pizzeria.fabulosa.web.controllers.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.pizzeria.fabulosa.security.utils.SecurityCookies;
import org.pizzeria.fabulosa.security.utils.UserSecurity;
import org.pizzeria.fabulosa.web.controllers.user.swagger.UserControllerSwagger;
import org.pizzeria.fabulosa.web.dto.api.Response;
import org.pizzeria.fabulosa.web.property.SecurityProperties;
import org.pizzeria.fabulosa.web.service.user.UserService;
import org.pizzeria.fabulosa.web.util.constant.ApiResponses;
import org.pizzeria.fabulosa.web.util.constant.ApiRoutes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.pizzeria.fabulosa.web.util.ResponseUtils.error;

@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.BASE + ApiRoutes.V1 + ApiRoutes.USER_BASE)
public class UserController implements UserControllerSwagger {

	private final UserService userService;

	private final SecurityProperties securityProperties;

	private final AuthenticationManager authenticationManager;

	@DeleteMapping
	public ResponseEntity<Response> deleteUser(
			@RequestParam Long id,
			@RequestParam String password,
			HttpServletRequest request,
			HttpServletResponse response) {
		if (!UserSecurity.valid(id)) {
			return UserSecurity.deny(request);
		}

		verifyPassword(password);

		boolean isError = userService.deleteUserById(password, id);

		if (isError) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error(this.getClass().getSimpleName(), ApiResponses.DUMMY_ACCOUNT_ERROR, request.getPathInfo()));
		}

		SecurityCookies.eatAllCookies(request, response, securityProperties.getCookies().getDomain());

		return ResponseEntity.ok().build();
	}

	private void verifyPassword(String password) {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		Jwt jwt = (Jwt) authentication.getPrincipal();
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwt.getSubject(), password));
	}
}