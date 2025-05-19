package org.pizzeria.fabulosa.web.config.docs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

import static org.pizzeria.fabulosa.web.util.constant.SecurityConstants.ACCESS_TOKEN;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Pizzeria Fabulosa API", version = "v1"))
@SecurityScheme(
		type = SecuritySchemeType.APIKEY,
		in = SecuritySchemeIn.COOKIE,
		name = ACCESS_TOKEN,
		description = """
					A JWT Token may be obtained by logging-in with an existing username ("donQuijote@gmail.com" - user id is "1") and password ("Password1").\s
				\t
					There's no need to add the value here; once the cookie is present, end-points that requiere it may be accessed.
				\t
					Default (local environment) domain for which cookies are set is "http://192.168.1.128:8080".
				\t
					Default (local environment) allowed origins are [http://192.168.1.128:4200, http://localhost:4200].
				\t
					These local environment defaults may be changed in "src/main/resources/application.yaml".
				\t"""
)
public class SpringDocsConfig {
}
