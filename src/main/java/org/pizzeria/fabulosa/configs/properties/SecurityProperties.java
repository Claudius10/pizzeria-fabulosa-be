package org.pizzeria.fabulosa.configs.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "security")
@Setter
@Getter
public class SecurityProperties {

	private String tokenIssuer;

	private Cookies cookies;

	private List<String> allowedOrigins;

	@AllArgsConstructor
	@Setter
	@Getter
	public static class Cookies {
		private String domain;
		private String sameSite;
		private Boolean httpOnly;
		private Boolean secure;
	}
}
