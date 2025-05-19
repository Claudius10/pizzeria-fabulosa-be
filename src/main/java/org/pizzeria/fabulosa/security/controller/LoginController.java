package org.pizzeria.fabulosa.security.controller;

import org.pizzeria.fabulosa.security.controller.swagger.LoginControllerSwagger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController implements LoginControllerSwagger {

	@PostMapping("login")
	public void login(@RequestParam String username, @RequestParam String password) {
		// ignore
		// Spring Security will intercept and process the request
	}
}
