package org.pizzeria.fabulosa.web.config.docs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Pizzeria Fabulosa API", version = "v1"))
@SecurityScheme(
		type = SecuritySchemeType.HTTP,
		in = SecuritySchemeIn.COOKIE,
		name = "Bearer Authentication",
		scheme = "bearer",
		bearerFormat = "JWT",
		description = """
					A JWT Token may be obtained by logging-in with an existing username ("donQuijote@gmail.com" - user id is "1") and password ("Password1").\s
				\t
					The token value needs to be extracted from the cookie "Pizzeria.Fabulosa.ACCESS_TOKEN" and entered in this form.
				\t
					Default (local environment) domain for which cookies are set is "http://192.168.1.128:8080".
				\t
					Default (local environment) allowed origins are [http://192.168.1.128:4200, http://localhost:4200].
				\t
					You may change these local environment defaults in "src/main/resources/application.yaml".
				\t"""
)
public class SpringDocsConfig {
}
