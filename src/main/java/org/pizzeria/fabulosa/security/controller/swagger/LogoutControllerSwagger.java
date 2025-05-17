package org.pizzeria.fabulosa.security.controller.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Logout")
public interface LogoutControllerSwagger {

	@Operation(operationId = "logout", summary = "Delete Access Token and ID Token Cookies")
	void logout();
}
