package org.pizzeria.fabulosa.security.controller;

import org.pizzeria.fabulosa.security.controller.swagger.LogoutControllerSwagger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Documenting the logout endpoint because Spring OpenAPI does not automatically detect and document it.
 */
@RestController
public class LogoutController implements LogoutControllerSwagger {

	@PostMapping("logout")
	public void logout() {
		// ignore
		// Spring Security will intercept and process the request
	}
}
